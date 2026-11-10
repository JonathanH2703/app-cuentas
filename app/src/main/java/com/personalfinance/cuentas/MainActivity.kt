package com.personalfinance.cuentas

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.personalfinance.cuentas.core.security.BiometricAuthManager
import com.personalfinance.cuentas.core.security.SecurityLifecycleObserver
import com.personalfinance.cuentas.core.security.SecurityPreferences
import com.personalfinance.cuentas.core.ui.theme.AppCuentasTheme
import com.personalfinance.cuentas.presentation.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var biometricAuthManager: BiometricAuthManager

    @Inject
    lateinit var securityLifecycleObserver: SecurityLifecycleObserver

    @Inject
    lateinit var securityPreferences: SecurityPreferences

    private var lockErrorMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Aplicar FLAG_SECURE para ocultar contenido en capturas de pantalla y vista de apps recientes
        applyFlagSecure(securityPreferences.isFlagSecureEnabled.value)

        // Observar cambios dinámicos en la preferencia de FLAG_SECURE
        lifecycleScope.launch {
            securityPreferences.isFlagSecureEnabled.collect { isEnabled ->
                applyFlagSecure(isEnabled)
            }
        }

        setContent {
            AppCuentasTheme {
                val isAppLocked by securityLifecycleObserver.isAppLocked.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        isAppLocked = isAppLocked,
                        lockErrorMessage = lockErrorMessage,
                        onUnlockRequested = { launchBiometricPrompt() }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Si la app está bloqueada al volver a primer plano, disparar el prompt biométrico automáticamente
        if (securityLifecycleObserver.isAppLocked.value) {
            launchBiometricPrompt()
        }
    }

    private fun applyFlagSecure(enable: Boolean) {
        if (enable) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    private fun launchBiometricPrompt() {
        lockErrorMessage = null
        biometricAuthManager.promptAuthentication(
            activity = this,
            title = getString(R.string.auth_title),
            subtitle = getString(R.string.auth_subtitle),
            description = getString(R.string.auth_description),
            onSuccess = {
                lockErrorMessage = null
                securityLifecycleObserver.unlock()
            },
            onError = { _, errorString ->
                lockErrorMessage = errorString
            },
            onFailed = {
                lockErrorMessage = getString(R.string.auth_failed)
            }
        )
    }
}
