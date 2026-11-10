package com.personalfinance.cuentas.domain.usecases

import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.IncomeDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.IncomeAllocationEntity
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import com.personalfinance.cuentas.domain.math.AllocationCalculator
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfirmIncomeUseCase @Inject constructor(
    private val incomeDao: IncomeDao,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val allocationDao: AllocationDao
) {
    /**
     * Confirma un ingreso: acredita el dinero, marca el ingreso como recibido y registra
     * el movimiento real (transacción INCOME). Si [linkedReceivableId] se indica, el
     * movimiento queda anclado a esa cuenta por cobrar ("Me deben").
     *
     * @return el id del movimiento (transacción) creado, o null si no había categoría.
     */
    suspend operator fun invoke(
        incomeId: Long,
        actualReceivedCents: Long? = null,
        targetAccountId: Long? = null,
        receivedMillis: Long = System.currentTimeMillis(),
        linkedReceivableId: Long? = null
    ): Long? {
        val income = incomeDao.getIncomeById(incomeId)
            ?: throw IllegalArgumentException("Ingreso con ID $incomeId no encontrado")

        if (income.isReceived) return null // Ya fue procesado

        val effectiveAmountCents = actualReceivedCents ?: income.amountCents
        val effectiveAccountId = targetAccountId ?: income.accountId

        // 1. Acreditar dinero real a la cuenta seleccionada
        val account = accountDao.getAccountById(effectiveAccountId).firstOrNull()
            ?: throw IllegalArgumentException("Cuenta con ID $effectiveAccountId no encontrada")

        val newBalance = account.currentBalanceCents + effectiveAmountCents
        accountDao.updateBalance(effectiveAccountId, newBalance)

        // 2. Anclar al valor real recibido y marcar como recibido
        if (effectiveAmountCents != income.amountCents || effectiveAccountId != income.accountId) {
            incomeDao.updateIncome(
                income.copy(
                    amountCents = effectiveAmountCents,
                    accountId = effectiveAccountId,
                    isReceived = true,
                    receivedDateMillis = receivedMillis
                )
            )
        } else {
            incomeDao.markAsReceived(incomeId, receivedMillis)
        }

        // 3. Registrar transacción tipo INCOME con el monto real
        val categories = categoryDao.getAllCategories().firstOrNull() ?: emptyList()
        val incomeCategory = categories.firstOrNull { it.name.contains("Ingreso", ignoreCase = true) }
            ?: categories.firstOrNull()

        var createdTransactionId: Long? = null
        if (incomeCategory != null) {
            val transaction = TransactionEntity(
                accountId = effectiveAccountId,
                categoryId = incomeCategory.id,
                amountCents = effectiveAmountCents,
                type = TransactionType.INCOME,
                dateMillis = receivedMillis,
                note = "Ingreso confirmado: ${income.description}",
                merchant = income.description,
                linkedReceivableId = linkedReceivableId
            )
            createdTransactionId = transactionDao.insertTransaction(transaction)
        }

        // 4. Distribuir automáticamente en los bloques del modelo activo (ej. 50/30/20) con el monto real recibido
        val activeModelWithBlocks = allocationDao.getActiveModelWithBlocks().firstOrNull()
        if (activeModelWithBlocks != null && activeModelWithBlocks.blocks.isNotEmpty()) {
            val splitResults = AllocationCalculator.splitIncomeAcrossBlocks(
                totalIncomeCents = effectiveAmountCents,
                blocks = activeModelWithBlocks.blocks
            )

            val allocationEntities = splitResults.map { result ->
                IncomeAllocationEntity(
                    incomeId = incomeId,
                    blockId = result.blockId,
                    assignedCents = result.assignedCents
                )
            }
            allocationDao.insertIncomeAllocations(allocationEntities)
        }

        return createdTransactionId
    }
}
