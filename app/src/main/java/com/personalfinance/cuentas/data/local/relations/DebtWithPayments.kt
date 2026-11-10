package com.personalfinance.cuentas.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.entities.DebtPaymentEntity

data class DebtWithPayments(
    @Embedded
    val debt: DebtEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "debt_id"
    )
    val payments: List<DebtPaymentEntity> = emptyList()
)
