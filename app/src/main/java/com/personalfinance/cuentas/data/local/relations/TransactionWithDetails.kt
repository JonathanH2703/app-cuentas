package com.personalfinance.cuentas.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity
import com.personalfinance.cuentas.data.local.entities.TransactionEntity

data class TransactionWithDetails(
    @Embedded
    val transaction: TransactionEntity,

    @Relation(
        parentColumn = "account_id",
        entityColumn = "id"
    )
    val account: AccountEntity,

    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: CategoryEntity,

    @Relation(
        parentColumn = "linked_debt_id",
        entityColumn = "id"
    )
    val linkedDebt: DebtEntity? = null,

    @Relation(
        parentColumn = "linked_recurring_item_id",
        entityColumn = "id"
    )
    val linkedRecurringItem: RecurringItemEntity? = null
)
