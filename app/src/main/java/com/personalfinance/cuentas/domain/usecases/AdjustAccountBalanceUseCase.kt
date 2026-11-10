package com.personalfinance.cuentas.domain.usecases

import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class AdjustAccountBalanceUseCase @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    suspend operator fun invoke(accountId: Long, newBalanceCents: Long, reason: String? = null) {
        val account = accountDao.getAccountById(accountId).firstOrNull()
            ?: throw IllegalArgumentException("Cuenta con ID $accountId no encontrada")

        val delta = newBalanceCents - account.currentBalanceCents
        if (delta == 0L) return

        accountDao.updateBalance(accountId, newBalanceCents)

        val categories = categoryDao.getAllCategories().firstOrNull() ?: emptyList()
        val defaultCat = categories.firstOrNull() ?: return

        val transaction = TransactionEntity(
            accountId = accountId,
            categoryId = defaultCat.id,
            amountCents = abs(delta),
            type = TransactionType.ADJUSTMENT,
            dateMillis = System.currentTimeMillis(),
            note = reason ?: "Ajuste manual de saldo (${if (delta > 0) "+" else "-"}${abs(delta)/100})"
        )
        transactionDao.insertTransaction(transaction)
    }
}
