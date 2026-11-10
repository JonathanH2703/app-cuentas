package com.personalfinance.cuentas.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalfinance.cuentas.core.security.SecurityPreferences
import com.personalfinance.cuentas.domain.backup.BackupManager
import com.personalfinance.cuentas.domain.backup.BackupPayload
import com.personalfinance.cuentas.domain.backup.RestoreSummary
import com.personalfinance.cuentas.domain.reminders.ReminderPreferences
import com.personalfinance.cuentas.domain.reminders.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class ExportState {
    object Idle : ExportState()
    data class ReadyToSave(val bytes: ByteArray, val suggestedFilename: String) : ExportState()
    data class Success(val message: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

sealed class RestoreState {
    object Idle : RestoreState()
    data class PasswordRequired(val bytes: ByteArray) : RestoreState()
    data class ConfirmPrompt(val bytes: ByteArray, val payload: BackupPayload, val passphrase: String?) : RestoreState()
    data class Success(val summary: RestoreSummary) : RestoreState()
    data class Error(val message: String) : RestoreState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securityPreferences: SecurityPreferences,
    private val reminderPreferences: ReminderPreferences,
    private val reminderScheduler: ReminderScheduler,
    private val backupManager: BackupManager
) : ViewModel() {

    val isFlagSecureEnabled = securityPreferences.isFlagSecureEnabled
    val isCardRemindersEnabled = reminderPreferences.isCardRemindersEnabled
    val isDailySummaryEnabled = reminderPreferences.isDailySummaryEnabled

    private val _isBiometricEnabled = MutableStateFlow(securityPreferences.isBiometricEnabled())
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _lockTimeoutSeconds = MutableStateFlow(securityPreferences.getLockTimeoutSeconds())
    val lockTimeoutSeconds: StateFlow<Long> = _lockTimeoutSeconds.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _restoreState = MutableStateFlow<RestoreState>(RestoreState.Idle)
    val restoreState: StateFlow<RestoreState> = _restoreState.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun setBiometricEnabled(enabled: Boolean) {
        securityPreferences.setBiometricEnabled(enabled)
        _isBiometricEnabled.value = enabled
    }

    fun setFlagSecureEnabled(enabled: Boolean) {
        securityPreferences.setFlagSecureEnabled(enabled)
    }

    fun setLockTimeoutSeconds(seconds: Long) {
        securityPreferences.setLockTimeoutSeconds(seconds)
        _lockTimeoutSeconds.value = seconds
    }

    fun setCardRemindersEnabled(enabled: Boolean) {
        reminderPreferences.setCardRemindersEnabled(enabled)
        reminderScheduler.scheduleDailyReminders()
    }

    fun setDailySummaryEnabled(enabled: Boolean) {
        reminderPreferences.setDailySummaryEnabled(enabled)
    }

    fun exportEncryptedBackup(passphrase: String) {
        viewModelScope.launch {
            try {
                if (passphrase.isBlank()) {
                    _exportState.value = ExportState.Error("La contraseña no puede estar vacía")
                    return@launch
                }
                val bytes = backupManager.exportBackup(passphrase)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename = "RespaldoFinanzas_$timeStamp.enc"
                _exportState.value = ExportState.ReadyToSave(bytes, filename)
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.localizedMessage ?: "Error al exportar")
            }
        }
    }

    fun exportPlainJsonBackup() {
        viewModelScope.launch {
            try {
                val bytes = backupManager.exportBackup(passphrase = null)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename = "RespaldoFinanzas_$timeStamp.json"
                _exportState.value = ExportState.ReadyToSave(bytes, filename)
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.localizedMessage ?: "Error al exportar JSON")
            }
        }
    }

    fun onFileSelectedForRestore(uri: Uri) {
        viewModelScope.launch {
            try {
                val bytes = readBytesFromUri(uri)
                if (bytes == null || bytes.isEmpty()) {
                    _restoreState.value = RestoreState.Error("El archivo seleccionado está vacío o no se pudo leer")
                    return@launch
                }

                val isEncrypted = com.personalfinance.cuentas.domain.backup.BackupCryptoEngine.isEncrypted(bytes)
                if (isEncrypted) {
                    _restoreState.value = RestoreState.PasswordRequired(bytes)
                } else {
                    val payload = backupManager.inspectBackup(bytes, passphrase = null)
                    _restoreState.value = RestoreState.ConfirmPrompt(bytes, payload, passphrase = null)
                }
            } catch (e: Exception) {
                _restoreState.value = RestoreState.Error("Archivo no válido: ${e.localizedMessage}")
            }
        }
    }

    fun onPasswordEnteredForRestore(bytes: ByteArray, passphrase: String) {
        viewModelScope.launch {
            try {
                val payload = backupManager.inspectBackup(bytes, passphrase)
                _restoreState.value = RestoreState.ConfirmPrompt(bytes, payload, passphrase)
            } catch (e: Exception) {
                _restoreState.value = RestoreState.Error("Contraseña incorrecta o archivo dañado")
            }
        }
    }

    fun executeRestore(bytes: ByteArray, passphrase: String?) {
        viewModelScope.launch {
            try {
                val summary = backupManager.restoreBackup(bytes, passphrase)
                _restoreState.value = RestoreState.Success(summary)
                _userMessage.emit("¡Restauración exitosa! Se cargaron ${summary.accountsCount} cuentas y ${summary.transactionsCount} movimientos.")
            } catch (e: Exception) {
                _restoreState.value = RestoreState.Error("Error al restaurar: ${e.localizedMessage}")
            }
        }
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }

    fun resetRestoreState() {
        _restoreState.value = RestoreState.Idle
    }

    private fun readBytesFromUri(uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { it.readBytes() }
        } catch (e: Exception) {
            null
        }
    }
}
