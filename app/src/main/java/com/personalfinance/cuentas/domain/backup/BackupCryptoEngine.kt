package com.personalfinance.cuentas.domain.backup

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCryptoEngine {

    private val MAGIC_HEADER = byteArrayOf(
        'F'.code.toByte(),
        'I'.code.toByte(),
        'N'.code.toByte(),
        'E'.code.toByte(),
        'N'.code.toByte(),
        'C'.code.toByte(),
        1.toByte() // Formato v1
    )

    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 12 // GCM estándar
    private const val GCM_TAG_LENGTH = 128
    private const val PBKDF2_ITERATIONS = 100_000
    private const val KEY_LENGTH_BITS = 256
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    fun isEncrypted(bytes: ByteArray): Boolean {
        if (bytes.size < MAGIC_HEADER.size + SALT_LENGTH + IV_LENGTH) return false
        for (i in MAGIC_HEADER.indices) {
            if (bytes[i] != MAGIC_HEADER[i]) return false
        }
        return true
    }

    fun encrypt(data: ByteArray, passphrase: CharArray): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH).apply { random.nextBytes(this) }
        val iv = ByteArray(IV_LENGTH).apply { random.nextBytes(this) }

        val secretKey = deriveKey(passphrase, salt)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        val ciphertextWithTag = cipher.doFinal(data)

        val output = ByteArray(MAGIC_HEADER.size + SALT_LENGTH + IV_LENGTH + ciphertextWithTag.size)
        var offset = 0

        System.arraycopy(MAGIC_HEADER, 0, output, offset, MAGIC_HEADER.size)
        offset += MAGIC_HEADER.size

        System.arraycopy(salt, 0, output, offset, SALT_LENGTH)
        offset += SALT_LENGTH

        System.arraycopy(iv, 0, output, offset, IV_LENGTH)
        offset += IV_LENGTH

        System.arraycopy(ciphertextWithTag, 0, output, offset, ciphertextWithTag.size)

        return output
    }

    fun decrypt(encryptedBytes: ByteArray, passphrase: CharArray): ByteArray {
        require(isEncrypted(encryptedBytes)) { "El archivo de respaldo no tiene el formato cifrado válido" }

        var offset = MAGIC_HEADER.size

        val salt = ByteArray(SALT_LENGTH)
        System.arraycopy(encryptedBytes, offset, salt, 0, SALT_LENGTH)
        offset += SALT_LENGTH

        val iv = ByteArray(IV_LENGTH)
        System.arraycopy(encryptedBytes, offset, iv, 0, IV_LENGTH)
        offset += IV_LENGTH

        val ciphertextLength = encryptedBytes.size - offset
        val ciphertextWithTag = ByteArray(ciphertextLength)
        System.arraycopy(encryptedBytes, offset, ciphertextWithTag, 0, ciphertextLength)

        val secretKey = deriveKey(passphrase, salt)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))

        return cipher.doFinal(ciphertextWithTag)
    }

    private fun deriveKey(passphrase: CharArray, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(passphrase, salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val rawKey = factory.generateSecret(spec).encoded
        return SecretKeySpec(rawKey, "AES")
    }
}
