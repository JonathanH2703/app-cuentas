package com.personalfinance.cuentas.core.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import javax.inject.Singleton

sealed class BiometricStatus {
    object Available : BiometricStatus()
    data class Unavailable(val reason: String) : BiometricStatus()
}

@Singleton
class BiometricAuthManager @Inject constructor(
    private val context: Context
) {

    private val authenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL

    /**
     * Verifica si el dispositivo (Honor 400 Smart) soporta biometría o PIN de desbloqueo.
     */
    fun checkBiometricAvailability(): BiometricStatus {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.Unavailable("Hardware biométrico no disponible")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.Unavailable("Sensor biométrico ocupado o no disponible temporalmente")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.Unavailable("No hay huellas, rostro o PIN registrados en el teléfono")
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.Unavailable("Actualización de seguridad requerida")
            else -> BiometricStatus.Unavailable("Autenticación no disponible")
        }
    }

    /**
     * Lanza el diálogo del sistema BiometricPrompt. Si falla la biometría, permite el fallback automático
     * a PIN o patrón del dispositivo.
     */
    fun promptAuthentication(
        activity: FragmentActivity,
        title: String = "Seguridad Financiera",
        subtitle: String = "Acceso a tus finanzas personales",
        description: String = "Usa tu huella, rostro o PIN del dispositivo",
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errorMessage: String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errorCode, errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)
    }
}
