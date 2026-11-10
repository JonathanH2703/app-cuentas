package com.personalfinance.cuentas.domain.usecases

import com.personalfinance.cuentas.data.local.dao.DebtDao
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.domain.schedule.DueDates
import com.personalfinance.cuentas.domain.usecases.debts.RecordDebtPaymentUseCase
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ancla (o desancla) una transacción a una obligación: la cuota de un crédito o un gasto fijo mensual.
 *
 * Además del vínculo, aplica el efecto de "obligación cumplida":
 *  - Deuda: registra el pago (reduce el saldo del crédito) y adelanta la próxima cuota pendiente.
 *  - Gasto fijo: adelanta el próximo vencimiento un mes.
 *
 * Si el ancla cambia o se elimina, se revierte el efecto anterior para no duplicar ni dejar saldos
 * inconsistentes en las gráficas y cálculos.
 */
@Singleton
class AnchorTransactionUseCase @Inject constructor(
    private val transactionDao: TransactionDao,
    private val debtDao: DebtDao,
    private val recurringItemDao: RecurringItemDao,
    private val recordDebtPaymentUseCase: RecordDebtPaymentUseCase
) {
    suspend operator fun invoke(
        transactionId: Long,
        linkedDebtId: Long?,
        linkedRecurringItemId: Long?
    ) {
        val transaction = transactionDao.getTransactionById(transactionId) ?: return
        val previousDebtId = transaction.linkedDebtId
        val previousRecurringId = transaction.linkedRecurringItemId

        if (previousDebtId != null && previousDebtId != linkedDebtId) {
            revertDebtPayment(previousDebtId, transaction.amountCents, transaction.dateMillis)
        }
        if (previousRecurringId != null && previousRecurringId != linkedRecurringItemId) {
            revertRecurringCycle(previousRecurringId)
        }

        transactionDao.updateAnchor(transactionId, linkedDebtId, linkedRecurringItemId)

        if (linkedDebtId != null && linkedDebtId != previousDebtId) {
            recordDebtPaymentUseCase(
                debtId = linkedDebtId,
                amountPaidCents = transaction.amountCents,
                isExtraPayment = false,
                paymentDateMillis = transaction.dateMillis
            )
        }
        if (linkedRecurringItemId != null && linkedRecurringItemId != previousRecurringId) {
            advanceRecurringCycle(linkedRecurringItemId)
        }
    }

    private suspend fun advanceRecurringCycle(recurringItemId: Long) {
        val item = recurringItemDao.getRecurringItemById(recurringItemId).firstOrNull() ?: return
        val dueDate = DueDates.toLocalDate(item.nextDueDateMillis)
        recurringItemDao.updateNextDueDate(
            recurringItemId,
            DueDates.toMillis(DueDates.shiftMonths(dueDate, 1))
        )
    }

    private suspend fun revertRecurringCycle(recurringItemId: Long) {
        val item = recurringItemDao.getRecurringItemById(recurringItemId).firstOrNull() ?: return
        val dueDate = DueDates.toLocalDate(item.nextDueDateMillis)
        recurringItemDao.updateNextDueDate(
            recurringItemId,
            DueDates.toMillis(DueDates.shiftMonths(dueDate, -1))
        )
    }

    private suspend fun revertDebtPayment(debtId: Long, amountCents: Long, dateMillis: Long) {
        val payment = debtDao.findPayment(debtId, amountCents, dateMillis)
        if (payment != null) {
            debtDao.restoreBalance(debtId, payment.principalPaidCents)
            debtDao.deletePayment(payment)
        }
        val debt = debtDao.getDebtById(debtId).firstOrNull() ?: return
        val dueDate = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, LocalDate.now())
        debtDao.updateNextDueDate(debtId, DueDates.toMillis(DueDates.shiftMonths(dueDate, -1)))
    }
}
