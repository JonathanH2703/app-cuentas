package com.personalfinance.cuentas.domain.usecases.creditcards

import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.CreditCardDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import com.personalfinance.cuentas.domain.math.FinancialFormulas
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordCardPurchaseUseCase @Inject constructor(
    private val creditCardDao: CreditCardDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val allocationDao: AllocationDao
) {
    suspend operator fun invoke(
        cardId: Long,
        merchant: String,
        amountCents: Long,
        installmentsTotal: Int,
        categoryId: Long,
        purchaseDateMillis: Long = System.currentTimeMillis(),
        isInterestFree: Boolean = false
    ): Long {
        require(amountCents > 0) { "El monto de compra debe ser mayor a cero" }
        require(installmentsTotal >= 1) { "Las cuotas deben ser al menos 1" }

        val cardWithAccount = creditCardDao.getCreditCardById(cardId).firstOrNull()
            ?: throw IllegalArgumentException("Tarjeta con ID $cardId no encontrada")

        val category = categoryDao.getCategoryById(categoryId)
            ?: throw IllegalArgumentException("Categoría con ID $categoryId no encontrada")

        val activeModel = allocationDao.getActiveModelWithBlocks().firstOrNull()
        val blockId = activeModel?.blocks?.find { it.type == category.defaultBlockType }?.id

        val monthlyRate = if (installmentsTotal > 1 && !isInterestFree) {
            FinancialFormulas.eaToMonthlyRate(cardWithAccount.card.interestRateEa)
        } else {
            0.0
        }

        // 1. Guardar la compra en la tarjeta
        val purchase = CardPurchaseEntity(
            cardId = cardId,
            merchant = merchant.trim(),
            purchaseDateMillis = purchaseDateMillis,
            totalAmountCents = amountCents,
            installmentsTotal = installmentsTotal,
            installmentsPaid = 0,
            interestRateNm = monthlyRate * 100.0,
            isFullyPaid = false
        )
        val purchaseId = creditCardDao.insertPurchase(purchase)

        // 2. Registrar la transacción en el historial
        val note = if (installmentsTotal > 1) "Compra a $installmentsTotal cuotas" else "Compra a 1 cuota"
        val transaction = TransactionEntity(
            accountId = cardWithAccount.card.accountId,
            categoryId = categoryId,
            blockId = blockId,
            amountCents = amountCents,
            type = TransactionType.EXPENSE,
            dateMillis = purchaseDateMillis,
            note = note,
            merchant = merchant.trim()
        )
        transactionDao.insertTransaction(transaction)

        return purchaseId
    }
}
