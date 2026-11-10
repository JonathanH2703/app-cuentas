package com.personalfinance.cuentas.domain.usecases.debts

import com.personalfinance.cuentas.data.local.dao.DebtDao
import com.personalfinance.cuentas.data.local.entities.DebtPaymentEntity
import com.personalfinance.cuentas.domain.math.FinancialFormulas
import com.personalfinance.cuentas.domain.schedule.DueDates
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

@Singleton
class RecordDebtPaymentUseCase @Inject constructor(
    private val debtDao: DebtDao
) {
    suspend operator fun invoke(
        debtId: Long,
        amountPaidCents: Long,
        isExtraPayment: Boolean = false,
        paymentDateMillis: Long = System.currentTimeMillis()
    ): Long {
        require(amountPaidCents > 0) { "El monto del pago debe ser mayor a cero" }

        val debt = debtDao.getDebtById(debtId).firstOrNull()
            ?: throw IllegalArgumentException("Deuda con ID $debtId no encontrada")

        // 3. Calcular interés y abono a capital (restando el seguro)
        val monthlyRate = FinancialFormulas.eaToMonthlyRate(debt.interestRateEa)

        val interestCents: Long
        val principalCents: Long

        if (isExtraPayment) {
            // Un abono extraordinario va 100% a capital (beneficio financiero directo)
            interestCents = 0L
            principalCents = min(amountPaidCents, debt.currentBalanceCents)
        } else {
            interestCents = min(debt.currentBalanceCents, (debt.currentBalanceCents.toDouble() * monthlyRate).roundToLong())
            
            // El seguro se descuenta primero, luego el interés, y el resto va a capital
            val insuranceCents = debt.monthlyInsuranceCents
            principalCents = min(debt.currentBalanceCents, max(0L, amountPaidCents - interestCents - insuranceCents))
        }

        val newBalance = max(0L, debt.currentBalanceCents - principalCents)
        val settled = newBalance == 0L

        debtDao.updateBalance(debtId, newBalance, settled)

        val payment = DebtPaymentEntity(
            debtId = debtId,
            paymentDateMillis = paymentDateMillis,
            totalPaidCents = amountPaidCents,
            principalPaidCents = principalCents,
            interestPaidCents = interestCents,
            isExtraPayment = isExtraPayment
        )
        val paymentId = debtDao.insertPayment(payment)

        // La cuota mensual ya quedó cubierta: la próxima obligación es la del mes siguiente.
        // Así los cálculos y gráficas no vuelven a proyectar una cuota ya pagada.
        if (!isExtraPayment && !settled) {
            val nextDue = DueDates.shiftMonths(
                DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, LocalDate.now()),
                1
            )
            debtDao.updateNextDueDate(debtId, DueDates.toMillis(nextDue))
        }

        return paymentId
    }
}
