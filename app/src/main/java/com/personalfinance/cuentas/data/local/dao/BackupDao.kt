package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.AllocationModelEntity
import com.personalfinance.cuentas.data.local.entities.BudgetEntity
import com.personalfinance.cuentas.data.local.entities.CaptureRuleEntity
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.data.local.entities.ContactEntity
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.entities.DebtPaymentEntity
import com.personalfinance.cuentas.data.local.entities.IncomeAllocationEntity
import com.personalfinance.cuentas.data.local.entities.IncomeEntity
import com.personalfinance.cuentas.data.local.entities.MerchantLearningEntity
import com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity
import com.personalfinance.cuentas.data.local.entities.ReceivableEntity
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity
import com.personalfinance.cuentas.data.local.entities.SavingsGoalEntity
import com.personalfinance.cuentas.data.local.entities.TagEntity
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionTagEntity

@Dao
interface BackupDao {

    // === QUERIES FOR EXPORT ===
    @Query("SELECT * FROM accounts ORDER BY id ASC")
    suspend fun getAllAccounts(): List<AccountEntity>

    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT * FROM tags ORDER BY id ASC")
    suspend fun getAllTags(): List<TagEntity>

    @Query("SELECT * FROM allocation_models ORDER BY id ASC")
    suspend fun getAllAllocationModels(): List<AllocationModelEntity>

    @Query("SELECT * FROM allocation_blocks ORDER BY id ASC")
    suspend fun getAllAllocationBlocks(): List<AllocationBlockEntity>

    @Query("SELECT * FROM transactions ORDER BY id ASC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM transaction_tags")
    suspend fun getAllTransactionTags(): List<TransactionTagEntity>

    @Query("SELECT * FROM incomes ORDER BY id ASC")
    suspend fun getAllIncomes(): List<IncomeEntity>

    @Query("SELECT * FROM income_allocations ORDER BY id ASC")
    suspend fun getAllIncomeAllocations(): List<IncomeAllocationEntity>

    @Query("SELECT * FROM credit_cards ORDER BY id ASC")
    suspend fun getAllCreditCards(): List<CreditCardEntity>

    @Query("SELECT * FROM card_purchases ORDER BY id ASC")
    suspend fun getAllCardPurchases(): List<CardPurchaseEntity>

    @Query("SELECT * FROM debts ORDER BY id ASC")
    suspend fun getAllDebts(): List<DebtEntity>

    @Query("SELECT * FROM debt_payments ORDER BY id ASC")
    suspend fun getAllDebtPayments(): List<DebtPaymentEntity>

    @Query("SELECT * FROM budgets ORDER BY id ASC")
    suspend fun getAllBudgets(): List<BudgetEntity>

    @Query("SELECT * FROM savings_goals ORDER BY id ASC")
    suspend fun getAllSavingsGoals(): List<SavingsGoalEntity>

    @Query("SELECT * FROM recurring_items ORDER BY id ASC")
    suspend fun getAllRecurringItems(): List<RecurringItemEntity>

    @Query("SELECT * FROM capture_rules ORDER BY id ASC")
    suspend fun getAllCaptureRules(): List<CaptureRuleEntity>

    @Query("SELECT * FROM pending_transactions ORDER BY id ASC")
    suspend fun getAllPendingTransactions(): List<PendingTransactionEntity>

    @Query("SELECT * FROM merchant_learnings ORDER BY id ASC")
    suspend fun getAllMerchantLearnings(): List<MerchantLearningEntity>

    @Query("SELECT * FROM contacts ORDER BY id ASC")
    suspend fun getAllContacts(): List<ContactEntity>

    @Query("SELECT * FROM receivables ORDER BY id ASC")
    suspend fun getAllReceivables(): List<ReceivableEntity>

    // === INSERTS FOR RESTORE ===
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocationModels(models: List<AllocationModelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocationBlocks(blocks: List<AllocationBlockEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionTags(transactionTags: List<TransactionTagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomes(incomes: List<IncomeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomeAllocations(allocations: List<IncomeAllocationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditCards(cards: List<CreditCardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardPurchases(purchases: List<CardPurchaseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebts(debts: List<DebtEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtPayments(payments: List<DebtPaymentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<BudgetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoals(goals: List<SavingsGoalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringItems(items: List<RecurringItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaptureRules(rules: List<CaptureRuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingTransactions(pending: List<PendingTransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantLearnings(learnings: List<MerchantLearningEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceivables(receivables: List<ReceivableEntity>)

    // === CLEAR TABLES IN SAFE REVERSE FK ORDER ===
    @Query("DELETE FROM transaction_tags")
    suspend fun clearTransactionTags()

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM card_purchases")
    suspend fun clearCardPurchases()

    @Query("DELETE FROM credit_cards")
    suspend fun clearCreditCards()

    @Query("DELETE FROM debt_payments")
    suspend fun clearDebtPayments()

    @Query("DELETE FROM debts")
    suspend fun clearDebts()

    @Query("DELETE FROM income_allocations")
    suspend fun clearIncomeAllocations()

    @Query("DELETE FROM incomes")
    suspend fun clearIncomes()

    @Query("DELETE FROM recurring_items")
    suspend fun clearRecurringItems()

    @Query("DELETE FROM budgets")
    suspend fun clearBudgets()

    @Query("DELETE FROM savings_goals")
    suspend fun clearSavingsGoals()

    @Query("DELETE FROM allocation_blocks")
    suspend fun clearAllocationBlocks()

    @Query("DELETE FROM allocation_models")
    suspend fun clearAllocationModels()

    @Query("DELETE FROM accounts")
    suspend fun clearAccounts()

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Query("DELETE FROM tags")
    suspend fun clearTags()

    @Query("DELETE FROM pending_transactions")
    suspend fun clearPendingTransactions()

    @Query("DELETE FROM merchant_learnings")
    suspend fun clearMerchantLearnings()

    @Query("DELETE FROM capture_rules")
    suspend fun clearCaptureRules()

    @Query("DELETE FROM receivables")
    suspend fun clearReceivables()

    @Query("DELETE FROM contacts")
    suspend fun clearContacts()
}
