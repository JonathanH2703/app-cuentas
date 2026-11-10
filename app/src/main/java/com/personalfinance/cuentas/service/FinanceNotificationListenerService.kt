package com.personalfinance.cuentas.service

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.personalfinance.cuentas.data.local.entities.PendingSource
import com.personalfinance.cuentas.domain.capture.ColombianBankParser
import com.personalfinance.cuentas.domain.usecases.capture.ProcessBankCaptureUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FinanceNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var processBankCaptureUseCase: ProcessBankCaptureUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        @Volatile
        var instance: FinanceNotificationListenerService? = null
            private set

        @Volatile
        var isConnected: Boolean = false
            private set

        /**
         * Fuerza el escaneo de todas las notificaciones activas actualmente en la barra de estado.
         */
        fun scanActiveNotifications() {
            instance?.processAllActiveNotifications()
        }

        /**
         * Solicita reconectar el servicio en caso de desconexión por optimización de batería o actualización de APK.
         */
        fun ensureConnected(context: Context) {
            try {
                requestRebind(ComponentName(context, FinanceNotificationListenerService::class.java))
            } catch (_: Exception) {}
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        isConnected = true
        // Al conectar o reconectar, procesar inmediatamente notificaciones activas que hayan quedado en la bandeja
        processAllActiveNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isConnected = false
        if (instance == this) {
            instance = null
        }
        // Intentar auto-reconectar de inmediato
        try {
            requestRebind(ComponentName(this, FinanceNotificationListenerService::class.java))
        } catch (_: Exception) {}
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return
        processStatusBarNotification(sbn)
    }

    fun processAllActiveNotifications() {
        serviceScope.launch {
            try {
                val notifications = activeNotifications ?: return@launch
                for (sbn in notifications) {
                    processStatusBarNotification(sbn)
                }
            } catch (_: Exception) {
                // Manejo resiliente
            }
        }
    }

    private fun processStatusBarNotification(sbn: StatusBarNotification) {
        val packageName = sbn.packageName ?: return

        // Ignorar notificaciones de nuestra propia aplicación para evitar bucles
        if (packageName == this.packageName) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        // Extracción exhaustiva de todos los campos de texto posibles en Android
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val titleBig = extras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString() ?: ""
        val infoText = extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString() ?: ""
        val summaryText = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString() ?: ""
        val ticker = notification.tickerText?.toString() ?: ""

        // Para notificaciones estilo Inbox / correos agrupados (Gmail, Outlook)
        val linesText = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
            ?.mapNotNull { it?.toString() }
            ?.filter { it.isNotBlank() }
            ?.joinToString(" | ") ?: ""

        // Construir contenido completo sin perder detalles
        val contentParts = listOf(title, titleBig, subText, text, bigText, infoText, summaryText, linesText, ticker)
            .filter { it.isNotBlank() }
            .distinct()

        val combinedContent = contentParts.joinToString(" ")
        if (combinedContent.isBlank()) return

        // Contexto de remitente extendido (paquete + títulos) para identificar el banco incluso desde correos
        val senderContext = "$packageName $title $subText"

        val parsed = ColombianBankParser.parse(
            senderOrPackage = senderContext,
            rawText = combinedContent,
            source = PendingSource.NOTIFICATION,
            timestampMillis = if (sbn.postTime > 0L) sbn.postTime else System.currentTimeMillis()
        )

        if (parsed != null) {
            serviceScope.launch {
                try {
                    processBankCaptureUseCase.execute(parsed)
                } catch (_: Exception) {
                    // Resiliencia en segundo plano
                }
            }
        }
    }
}
