package com.personalfinance.cuentas.domain.usecases

import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.TagDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionTagEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordExpenseUseCase @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val allocationDao: AllocationDao,
    private val tagDao: TagDao
) {
    suspend operator fun invoke(
        accountId: Long,
        categoryId: Long,
        amountCents: Long,
        note: String? = null,
        merchant: String? = null,
        dateMillis: Long = System.currentTimeMillis(),
        tagIds: List<Long> = emptyList(),
        isLeisure: Boolean = false
    ): Long {
        require(amountCents > 0) { "El monto del gasto debe ser mayor a cero" }

        // 1. Obtener la cuenta y descontar el saldo
        val account = accountDao.getAccountById(accountId).firstOrNull()
            ?: throw IllegalArgumentException("Cuenta con ID $accountId no encontrada")

        val newBalance = account.currentBalanceCents - amountCents
        accountDao.updateBalance(accountId, newBalance)

        // 2. Resolver el bloque de asignación (ej. Necesidades, Deseos, Deudas) según la categoría
        val category = categoryDao.getCategoryById(categoryId)
        val activeModelWithBlocks = allocationDao.getActiveModelWithBlocks().firstOrNull()

        val matchingBlockId = if (category != null && activeModelWithBlocks != null) {
            val block = activeModelWithBlocks.blocks.firstOrNull { it.type == category.defaultBlockType }
                ?: activeModelWithBlocks.blocks.firstOrNull()
            block?.id
        } else null

        // 3. Registrar la transacción con su bloque asociado
        val transaction = TransactionEntity(
            accountId = accountId,
            categoryId = categoryId,
            blockId = matchingBlockId,
            amountCents = amountCents,
            type = TransactionType.EXPENSE,
            dateMillis = dateMillis,
            note = note,
            merchant = merchant,
            isLeisure = isLeisure
        )
        val transactionId = transactionDao.insertTransaction(transaction)

        // 4. Asociar etiquetas si se especificaron
        if (tagIds.isNotEmpty()) {
            val relationTags = tagIds.map { tagId ->
                TransactionTagEntity(transactionId = transactionId, tagId = tagId)
            }
            tagDao.insertTransactionTags(relationTags)
        }

        return transactionId
    }
}
