package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debt_payments",
    foreignKeys = [
        ForeignKey(
            entity = DebtEntity::class,
            parentColumns = ["id"],
            childColumns = ["debt_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["debt_id"])
    ]
)
data class DebtPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "debt_id")
    val debtId: Long,

    @ColumnInfo(name = "payment_date_millis")
    val paymentDateMillis: Long,

    @ColumnInfo(name = "total_paid_cents")
    val totalPaidCents: Long,

    @ColumnInfo(name = "principal_paid_cents")
    val principalPaidCents: Long,

    @ColumnInfo(name = "interest_paid_cents")
    val interestPaidCents: Long,

    @ColumnInfo(name = "is_extra_payment")
    val isExtraPayment: Boolean = false
)
