package com.personalfinance.cuentas.domain.backup

import androidx.room.withTransaction
import com.personalfinance.cuentas.core.database.AppDatabase
import com.personalfinance.cuentas.data.local.dao.BackupDao
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    private val backupDao: BackupDao,
    private val appDatabase: AppDatabase
) {

    /**
     * Exporta todos los datos financieros locales.
     * Si [passphrase] se proporciona y no está en blanco, el archivo resultante es
     * un binario cifrado AES-256-GCM (.enc). Si es null o vacío, se exporta como JSON (.json).
     */
    suspend fun exportBackup(passphrase: String? = null): ByteArray {
        val payload = BackupPayload(
            schemaVersion = 1,
            exportedAtMillis = System.currentTimeMillis(),
            appVersion = "1.0.0",
            accounts = backupDao.getAllAccounts(),
            categories = backupDao.getAllCategories(),
            tags = backupDao.getAllTags(),
            allocationModels = backupDao.getAllAllocationModels(),
            allocationBlocks = backupDao.getAllAllocationBlocks(),
            transactions = backupDao.getAllTransactions(),
            transactionTags = backupDao.getAllTransactionTags(),
            incomes = backupDao.getAllIncomes(),
            incomeAllocations = backupDao.getAllIncomeAllocations(),
            creditCards = backupDao.getAllCreditCards(),
            cardPurchases = backupDao.getAllCardPurchases(),
            debts = backupDao.getAllDebts(),
            debtPayments = backupDao.getAllDebtPayments(),
            budgets = backupDao.getAllBudgets(),
            savingsGoals = backupDao.getAllSavingsGoals(),
            recurringItems = backupDao.getAllRecurringItems(),
            captureRules = backupDao.getAllCaptureRules(),
            pendingTransactions = backupDao.getAllPendingTransactions(),
            merchantLearnings = backupDao.getAllMerchantLearnings(),
            contacts = backupDao.getAllContacts(),
            receivables = backupDao.getAllReceivables()
        )

        val jsonString = BackupJsonSerializer.serialize(payload)
        val jsonBytes = jsonString.toByteArray(StandardCharsets.UTF_8)

        return if (!passphrase.isNullOrBlank()) {
            BackupCryptoEngine.encrypt(jsonBytes, passphrase.toCharArray())
        } else {
            jsonBytes
        }
    }

    /**
     * Inspecciona los bytes de una copia de seguridad para validar su integridad y
     * extraer los datos antes de aplicar la restauración en la base de datos.
     */
    fun inspectBackup(bytes: ByteArray, passphrase: String? = null): BackupPayload {
        val isEncrypted = BackupCryptoEngine.isEncrypted(bytes)
        val jsonBytes = if (isEncrypted) {
            if (passphrase.isNullOrBlank()) {
                throw IllegalArgumentException("Esta copia de seguridad está protegida con contraseña cifrada")
            }
            BackupCryptoEngine.decrypt(bytes, passphrase.toCharArray())
        } else {
            bytes
        }

        val jsonString = String(jsonBytes, StandardCharsets.UTF_8)
        return BackupJsonSerializer.deserialize(jsonString)
    }

    /**
     * Restaura una copia de seguridad en una transacción atómica Room.
     * Si la base de datos ya contiene información, se limpia en orden seguro de claves foráneas
     * y se reinserta toda la estructura respaldada.
     */
    suspend fun restoreBackup(bytes: ByteArray, passphrase: String? = null): RestoreSummary {
        val payload = inspectBackup(bytes, passphrase)

        appDatabase.withTransaction {
            // 1. Limpieza en orden inverso de dependencias FK
            backupDao.clearReceivables()
            backupDao.clearTransactionTags()
            backupDao.clearTransactions()
            backupDao.clearCardPurchases()
            backupDao.clearCreditCards()
            backupDao.clearDebtPayments()
            backupDao.clearDebts()
            backupDao.clearIncomeAllocations()
            backupDao.clearIncomes()
            backupDao.clearRecurringItems()
            backupDao.clearBudgets()
            backupDao.clearSavingsGoals()
            backupDao.clearAllocationBlocks()
            backupDao.clearAllocationModels()
            backupDao.clearAccounts()
            backupDao.clearCategories()
            backupDao.clearTags()
            backupDao.clearPendingTransactions()
            backupDao.clearMerchantLearnings()
            backupDao.clearCaptureRules()
            backupDao.clearContacts()

            // 2. Inserción en orden de jerarquía FK
            if (payload.allocationModels.isNotEmpty()) backupDao.insertAllocationModels(payload.allocationModels)
            if (payload.allocationBlocks.isNotEmpty()) backupDao.insertAllocationBlocks(payload.allocationBlocks)
            if (payload.categories.isNotEmpty()) backupDao.insertCategories(payload.categories)
            if (payload.accounts.isNotEmpty()) backupDao.insertAccounts(payload.accounts)
            if (payload.tags.isNotEmpty()) backupDao.insertTags(payload.tags)
            if (payload.contacts.isNotEmpty()) backupDao.insertContacts(payload.contacts)
            if (payload.transactions.isNotEmpty()) backupDao.insertTransactions(payload.transactions)
            if (payload.transactionTags.isNotEmpty()) backupDao.insertTransactionTags(payload.transactionTags)
            if (payload.incomes.isNotEmpty()) backupDao.insertIncomes(payload.incomes)
            if (payload.incomeAllocations.isNotEmpty()) backupDao.insertIncomeAllocations(payload.incomeAllocations)
            if (payload.creditCards.isNotEmpty()) backupDao.insertCreditCards(payload.creditCards)
            if (payload.cardPurchases.isNotEmpty()) backupDao.insertCardPurchases(payload.cardPurchases)
            if (payload.debts.isNotEmpty()) backupDao.insertDebts(payload.debts)
            if (payload.debtPayments.isNotEmpty()) backupDao.insertDebtPayments(payload.debtPayments)
            if (payload.budgets.isNotEmpty()) backupDao.insertBudgets(payload.budgets)
            if (payload.savingsGoals.isNotEmpty()) backupDao.insertSavingsGoals(payload.savingsGoals)
            if (payload.recurringItems.isNotEmpty()) backupDao.insertRecurringItems(payload.recurringItems)
            if (payload.captureRules.isNotEmpty()) backupDao.insertCaptureRules(payload.captureRules)
            if (payload.pendingTransactions.isNotEmpty()) backupDao.insertPendingTransactions(payload.pendingTransactions)
            if (payload.merchantLearnings.isNotEmpty()) backupDao.insertMerchantLearnings(payload.merchantLearnings)
            if (payload.receivables.isNotEmpty()) backupDao.insertReceivables(payload.receivables)
        }

        return RestoreSummary(
            accountsCount = payload.accounts.size,
            transactionsCount = payload.transactions.size,
            creditCardsCount = payload.creditCards.size,
            debtsCount = payload.debts.size,
            incomesCount = payload.incomes.size,
            exportedAtMillis = payload.exportedAtMillis
        )
    }
}
