package com.personalfinance.cuentas.domain.usecases.creditcards

import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.CreditCardDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import com.personalfinance.cuentas.domain.math.FinancialFormulas
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToLong

/**
 * Registra el pago de una tarjeta de crédito.
 *
 * Efectos:
 * 1. Debita el saldo de la [accountId] origen (el dinero sale realmente).
 * 2. Aplica el pago a las compras activas más antiguas primero, avanzando `installmentsPaid`
 *    y marcando `isFullyPaid` cuando se completan todas las cuotas.
 * 3. Crea una transacción de tipo ADJUSTMENT para que aparezca en el historial sin afectar
 *    los sobres de presupuesto (TAREA-02 original se mantiene: no afecta sobres, pero sí historial).
 */
@Singleton
class PayCreditCardUseCase @Inject constructor(
    private val creditCardDao: CreditCardDao,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val allocationDao: AllocationDao
) {
    suspend operator fun invoke(
        cardId: Long,
        accountId: Long,
        amountCents: Long,
        paymentDateMillis: Long = System.currentTimeMillis()
    ) {
        require(amountCents > 0L) { "El monto del pago debe ser mayor a cero" }

        val account = accountDao.getAccountById(accountId).firstOrNull()
            ?: throw IllegalArgumentException("Cuenta con ID $accountId no encontrada")

        // Valida que la tarjeta exista antes de mover dinero.
        val cardWithAccount = creditCardDao.getCreditCardById(cardId).firstOrNull()
            ?: throw IllegalArgumentException("Tarjeta con ID $cardId no encontrada")

        // 1. Debita el saldo de la cuenta origen (el dinero sale realmente).
        accountDao.updateBalance(accountId, account.currentBalanceCents - amountCents)

        // 2. Registrar la transacción en el historial como AJUSTE para que no afecte sobres
        // pero que el usuario vea que pagó la tarjeta.
        val category = categoryDao.getAllCategoriesSync().find { it.name.contains("Transferencia", ignoreCase = true) || it.name.contains("Ajuste", ignoreCase = true) }
        
        transactionDao.insertTransaction(
            TransactionEntity(
                accountId = accountId,
                categoryId = category?.id ?: 1L, // Categoría por defecto si no existe
                amountCents = amountCents,
                type = TransactionType.ADJUSTMENT,
                dateMillis = paymentDateMillis,
                note = "Pago Tarjeta: ${cardWithAccount.card.lastFourDigits ?: cardWithAccount.account.name}",
                merchant = "Pago Tarjeta"
            )
        )

        // 3. Aplica el pago a las compras activas ya facturadas (o con cuotas iniciadas).
        reduceCardDebt(cardId, amountCents, paymentDateMillis)
    }

    /**
     * Aplica un pago directamente a la deuda de la tarjeta SIN debitar ninguna cuenta.
     */
    suspend fun reduceCardDebt(
        cardId: Long,
        amountCents: Long,
        paymentDateMillis: Long = System.currentTimeMillis()
    ) {
        require(amountCents > 0L) { "El monto del pago debe ser mayor a cero" }

        val cardWithAccount = creditCardDao.getCreditCardById(cardId).firstOrNull()
            ?: throw IllegalArgumentException("Tarjeta con ID $cardId no encontrada")
        val card = cardWithAccount.card

        val today = java.time.Instant.ofEpochMilli(paymentDateMillis)
            .atZone(ZoneId.systemDefault()).toLocalDate()

        var remaining = amountCents
        val purchases = creditCardDao.getPurchasesForCard(cardId).first()
            .filter { !it.isFullyPaid }
            .filter {
                it.installmentsPaid >= 1 ||
                    FinancialFormulas.hasCutOffPassed(it.purchaseDateMillis, card.cutOffDay, today)
            }
            .sortedBy { it.purchaseDateMillis }

        for (purchase in purchases) {
            if (remaining <= 0L) break

            if (purchase.installmentsTotal <= 1) {
                // Compra a 1 cuota: se salda completa.
                if (remaining >= purchase.totalAmountCents) {
                    creditCardDao.updatePurchase(
                        purchase.copy(installmentsPaid = 1, isFullyPaid = true)
                    )
                    remaining -= purchase.totalAmountCents
                }
            } else {
                val monthlyRate = (purchase.interestRateNm / 100.0).coerceAtLeast(0.0)
                
                val installment = if (card.installmentType == 1) {
                    // Fintech: Capital/N + Interés sobre saldo actual
                    val capitalPerInstallment = purchase.totalAmountCents / purchase.installmentsTotal
                    val remainingPrincipal = purchase.totalAmountCents - (capitalPerInstallment * purchase.installmentsPaid)
                    val interestThisMonth = Math.round(remainingPrincipal.toDouble() * monthlyRate)
                    capitalPerInstallment + interestThisMonth
                } else {
                    FinancialFormulas.calculateInstallmentAmountCents(
                        purchase.totalAmountCents,
                        monthlyRate,
                        purchase.installmentsTotal
                    )
                }
                
                if (installment <= 0L) continue

                var paid = purchase.installmentsPaid
                while (remaining >= installment && paid < purchase.installmentsTotal) {
                    remaining -= installment
                    paid++
                }
                if (paid != purchase.installmentsPaid) {
                    creditCardDao.updatePurchase(
                        purchase.copy(
                            installmentsPaid = paid,
                            isFullyPaid = paid >= purchase.installmentsTotal
                        )
                    )
                }
            }
        }
    }
}
