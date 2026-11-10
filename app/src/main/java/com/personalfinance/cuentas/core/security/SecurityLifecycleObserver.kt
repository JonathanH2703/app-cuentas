package com.personalfinance.cuentas.core.security

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observador del ciclo de vida global de la aplicación (ProcessLifecycleOwner).
 * Detecta cuando la app pasa a segundo plano y bloquea la sesión tras cumplirse
 * el tiempo configurado (estilo app bancaria).
 */
@Singleton
class SecurityLifecycleObserver @Inject constructor(
    private val securityPreferences: SecurityPreferences
) : DefaultLifecycleObserver {

    private var lastBackgroundTimeMillis: Long = 0L

    // La app inicia bloqueada por defecto para exigir autenticación al abrirla
    private val _isAppLocked = MutableStateFlow(true)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (lastBackgroundTimeMillis != 0L) {
            val elapsedSeconds = (System.currentTimeMillis() - lastBackgroundTimeMillis) / 1000
            val timeoutSeconds = securityPreferences.getLockTimeoutSeconds()
            if (elapsedSeconds >= timeoutSeconds) {
                _isAppLocked.value = true
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        lastBackgroundTimeMillis = System.currentTimeMillis()
    }

    /**
     * Llamado tras una autenticación exitosa mediante BiometricPrompt o PIN
     */
    fun unlock() {
        _isAppLocked.value = false
        lastBackgroundTimeMillis = 0L
    }

    /**
     * Permite bloquear manualmente la app
     */
    fun lock() {
        _isAppLocked.value = true
        lastBackgroundTimeMillis = System.currentTimeMillis()
    }
}
