package com.personalfinance.cuentas.core.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de seguridad a nivel de hardware usando Android Keystore.
 * Custodia la llave maestra AES-GCM en el elemento seguro (TEE/SE) del Honor 400 Smart
 * y cifra/descifra la contraseña de 256 bits utilizada por SQLCipher para Room.
 */
@Singleton
class KeystoreManager @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "FinanceMasterDbKey"
        private const val PREFS_NAME = "secure_vault_prefs"
        private const val PREF_ENCRYPTED_PASSPHRASE = "enc_db_passphrase"
        private const val PREF_IV = "enc_db_iv"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val PASSPHRASE_BYTE_SIZE = 32 // 256 bits
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    /**
     * Obtiene la contraseña maestra para SQLCipher. Si no existe, se genera una
     * criptográficamente segura, se cifra con la llave del Keystore y se persiste.
     */
    @Synchronized
    fun getOrCreateDatabasePassphrase(): ByteArray {
        val encryptedPassphraseBase64 = prefs.getString(PREF_ENCRYPTED_PASSPHRASE, null)
        val ivBase64 = prefs.getString(PREF_IV, null)

        return if (encryptedPassphraseBase64 != null && ivBase64 != null) {
            decryptPassphrase(
                Base64.decode(encryptedPassphraseBase64, Base64.NO_WRAP),
                Base64.decode(ivBase64, Base64.NO_WRAP)
            )
        } else {
            val rawPassphrase = ByteArray(PASSPHRASE_BYTE_SIZE).apply {
                SecureRandom().nextBytes(this)
            }
            val (encryptedBytes, ivBytes) = encryptPassphrase(rawPassphrase)
            prefs.edit()
                .putString(PREF_ENCRYPTED_PASSPHRASE, Base64.encodeToString(encryptedBytes, Base64.NO_WRAP))
                .putString(PREF_IV, Base64.encodeToString(ivBytes, Base64.NO_WRAP))
                .commit()
            rawPassphrase
        }
    }

    private fun getOrCreateMasterKey(): SecretKey {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val parameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

            keyGenerator.init(parameterSpec)
            return keyGenerator.generateKey()
        }
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    private fun encryptPassphrase(rawPassphrase: ByteArray): Pair<ByteArray, ByteArray> {
        val secretKey = getOrCreateMasterKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(rawPassphrase)
        return Pair(encrypted, iv)
    }

    private fun decryptPassphrase(encryptedPassphrase: ByteArray, iv: ByteArray): ByteArray {
        val secretKey = getOrCreateMasterKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(encryptedPassphrase)
    }
}
