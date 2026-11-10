package com.personalfinance.cuentas.domain.backup

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

data class BackupPayload(
    val schemaVersion: Int = 1,
    val exportedAtMillis: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0.0",
    val accounts: List<AccountEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val tags: List<TagEntity> = emptyList(),
    val allocationModels: List<AllocationModelEntity> = emptyList(),
    val allocationBlocks: List<AllocationBlockEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val transactionTags: List<TransactionTagEntity> = emptyList(),
    val incomes: List<IncomeEntity> = emptyList(),
    val incomeAllocations: List<IncomeAllocationEntity> = emptyList(),
    val creditCards: List<CreditCardEntity> = emptyList(),
    val cardPurchases: List<CardPurchaseEntity> = emptyList(),
    val debts: List<DebtEntity> = emptyList(),
    val debtPayments: List<DebtPaymentEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList(),
    val savingsGoals: List<SavingsGoalEntity> = emptyList(),
    val recurringItems: List<RecurringItemEntity> = emptyList(),
    val captureRules: List<CaptureRuleEntity> = emptyList(),
    val pendingTransactions: List<PendingTransactionEntity> = emptyList(),
    val merchantLearnings: List<MerchantLearningEntity> = emptyList(),
    val contacts: List<ContactEntity> = emptyList(),
    val receivables: List<ReceivableEntity> = emptyList()
)

data class RestoreSummary(
    val accountsCount: Int,
    val transactionsCount: Int,
    val creditCardsCount: Int,
    val debtsCount: Int,
    val incomesCount: Int,
    val exportedAtMillis: Long
)
