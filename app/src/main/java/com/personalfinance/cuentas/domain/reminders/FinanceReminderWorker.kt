package com.personalfinance.cuentas.domain.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.personalfinance.cuentas.R
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.data.local.dao.CreditCardDao
import com.personalfinance.cuentas.data.local.dao.DebtDao
import com.personalfinance.cuentas.data.local.dao.EventDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.Calendar

class FinanceReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ReminderWorkerEntryPoint {
        fun creditCardDao(): CreditCardDao
        fun debtDao(): DebtDao
        fun eventDao(): EventDao
        fun reminderPreferences(): ReminderPreferences
    }

    companion object {
        const val CHANNEL_ID = "channel_finance_reminders"
        const val CHANNEL_NAME = "Recordatorios Financieros"
    }

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            ReminderWorkerEntryPoint::class.java
        )

        val reminderPreferences = entryPoint.reminderPreferences()
        if (!reminderPreferences.isCardRemindersEnabled()) {
            return Result.success()
        }

        val creditCardDao = entryPoint.creditCardDao()
        val debtDao = entryPoint.debtDao()

        val calendar = Calendar.getInstance()
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        createNotificationChannel()

        // 1. Verificar Tarjetas de Crédito
        val activeCards = creditCardDao.getAllCreditCardsWithAccountSync()
        for (cardWithAccount in activeCards) {
            val card = cardWithAccount.card
            val account = cardWithAccount.account
            val cardName = account.name

            // Corte de facturación
            val daysUntilCutOff = calculateDaysUntil(todayDay, card.cutOffDay, maxDaysInMonth)
            if (daysUntilCutOff == 0) {
                notifyReminder(
                    id = (card.id * 100 + 1).toInt(),
                    title = "Corte de Tarjeta Hoy: $cardName",
                    message = "Hoy cierra el ciclo de tu tarjeta. Las compras realizadas después de hoy pasarán al extracto del próximo mes."
                )
            } else if (daysUntilCutOff == 2) {
                notifyReminder(
                    id = (card.id * 100 + 2).toInt(),
                    title = "Corte próximo en 2 días: $cardName",
                    message = "El día ${card.cutOffDay} es tu corte de tarjeta. Revisa tus gastos antes del cierre de facturación."
                )
            }

            // Límite de pago
            val daysUntilPayment = calculateDaysUntil(todayDay, card.paymentDueDay, maxDaysInMonth)
            if (daysUntilPayment == 0) {
                notifyReminder(
                    id = (card.id * 100 + 3).toInt(),
                    title = "¡Fecha límite de pago hoy!: $cardName",
                    message = "Hoy vence el pago de tu tarjeta $cardName. Cancela a tiempo para evitar intereses de mora."
                )
            } else if (daysUntilPayment == 3) {
                notifyReminder(
                    id = (card.id * 100 + 4).toInt(),
                    title = "Pago pendiente en 3 días: $cardName",
                    message = "El día ${card.paymentDueDay} vence el pago de tu $cardName. Verifica tener fondos disponibles."
                )
            }
        }

        // 2. Verificar Créditos y Deudas Activas
        val activeDebts = debtDao.getActiveDebtsSync()
        for (debt in activeDebts) {
            val daysUntilDue = calculateDaysUntil(todayDay, debt.dueDayOfMonth, maxDaysInMonth)
            val installmentFormatted = CurrencyFormatter.formatCentsToCop(debt.monthlyInstallmentCents)

            if (daysUntilDue == 0) {
                notifyReminder(
                    id = (debt.id * 1000 + 1).toInt(),
                    title = "Cuota de crédito vence hoy: ${debt.name}",
                    message = "Hoy vence la cuota por $installmentFormatted de tu crédito ${debt.name}."
                )
            } else if (daysUntilDue == 2) {
                notifyReminder(
                    id = (debt.id * 1000 + 2).toInt(),
                    title = "Cuota de crédito en 2 días: ${debt.name}",
                    message = "El día ${debt.dueDayOfMonth} vence la cuota por $installmentFormatted de tu crédito ${debt.name}."
                )
            }
        }

        // 3. Verificar Eventos Personales (cumpleanos, aniversarios, etc.)
        val eventDao = entryPoint.eventDao()
        val activeEvents = eventDao.getAllActiveEventsSync()
        for (event in activeEvents) {
            val (daysUntil, _) = com.personalfinance.cuentas.presentation.events.EventsViewModel
                .daysUntilNextOccurrence(event.dayOfMonth, event.monthOfYear, event.year)
            if (daysUntil == 0) {
                val typeLabel = when (event.type) {
                    com.personalfinance.cuentas.data.local.entities.EventType.BIRTHDAY           -> "Cumpleanos"
                    com.personalfinance.cuentas.data.local.entities.EventType.ANNIVERSARY        -> "Aniversario"
                    com.personalfinance.cuentas.data.local.entities.EventType.CUSTOM             -> "Fecha especial"
                    com.personalfinance.cuentas.data.local.entities.EventType.OBLIGATION_ANNUAL  -> "Obligacion anual"
                    com.personalfinance.cuentas.data.local.entities.EventType.OBLIGATION_SPORADIC -> "Obligacion"
                }
                notifyReminder(
                    id = (event.id * 10000 + 1).toInt(),
                    title = "$typeLabel: ${event.title}",
                    message = if (event.personName != null)
                        "Hoy es el $typeLabel de ${event.title} (${event.personName})."
                    else
                        "Hoy es: ${event.title}."
                )
            } else if (daysUntil in 1..event.notifyDaysBefore) {
                notifyReminder(
                    id = (event.id * 10000 + 2).toInt(),
                    title = "En $daysUntil dias: ${event.title}",
                    message = if (event.personName != null)
                        "El ${event.dayOfMonth}/${event.monthOfYear} es el evento de ${event.personName}."
                    else
                        "Recuerda: ${event.title} en $daysUntil dias."
                )
            }
        }

        return Result.success()
    }

    private fun calculateDaysUntil(currentDay: Int, targetDay: Int, maxDaysInMonth: Int): Int {
        val normalizedTarget = targetDay.coerceAtMost(maxDaysInMonth)
        return if (normalizedTarget >= currentDay) {
            normalizedTarget - currentDay
        } else {
            (maxDaysInMonth - currentDay) + normalizedTarget
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de fechas de corte, límites de pago de tarjetas y créditos"
                enableVibration(true)
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notifyReminder(id: Int, title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}
