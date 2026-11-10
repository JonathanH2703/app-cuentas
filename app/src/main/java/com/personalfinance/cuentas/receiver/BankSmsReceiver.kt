package com.personalfinance.cuentas.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
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
class BankSmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var processBankCaptureUseCase: ProcessBankCaptureUseCase

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return

        for (sms in messages) {
            val sender = sms.displayOriginatingAddress ?: sms.originatingAddress ?: ""
            val body = sms.displayMessageBody ?: sms.messageBody ?: ""

            val parsed = ColombianBankParser.parse(
                senderOrPackage = sender,
                rawText = body,
                source = PendingSource.SMS,
                timestampMillis = if (sms.timestampMillis > 0L) sms.timestampMillis else System.currentTimeMillis()
            )

            if (parsed != null) {
                val pendingResult = goAsync()
                receiverScope.launch {
                    try {
                        processBankCaptureUseCase.execute(parsed)
                    } catch (_: Exception) {
                        // Resiliente ante excepciones en segundo plano
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
