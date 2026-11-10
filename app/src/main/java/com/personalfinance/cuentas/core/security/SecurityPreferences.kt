package com.personalfinance.cuentas.core.security

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "security_config_prefs"
        private const val KEY_LOCK_TIMEOUT_SECONDS = "lock_timeout_seconds"
        private const val KEY_FLAG_SECURE = "flag_secure_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        const val DEFAULT_TIMEOUT_SECONDS = 60L // 1 minuto en background
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val _isFlagSecureEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_FLAG_SECURE, true)
    )
    val isFlagSecureEnabled: StateFlow<Boolean> = _isFlagSecureEnabled.asStateFlow()

    fun getLockTimeoutSeconds(): Long {
        return prefs.getLong(KEY_LOCK_TIMEOUT_SECONDS, DEFAULT_TIMEOUT_SECONDS)
    }

    fun setLockTimeoutSeconds(seconds: Long) {
        prefs.edit().putLong(KEY_LOCK_TIMEOUT_SECONDS, seconds).apply()
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun setFlagSecureEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FLAG_SECURE, enabled).apply()
        _isFlagSecureEnabled.value = enabled
    }
}
