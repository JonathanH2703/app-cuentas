package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "credit_cards",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["account_id"])
    ]
)
data class CreditCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "account_id")
    val accountId: Long,

    @ColumnInfo(name = "credit_limit_cents")
    val creditLimitCents: Long,

    @ColumnInfo(name = "cut_off_day")
    val cutOffDay: Int, // 1 - 31

    @ColumnInfo(name = "payment_due_day")
    val paymentDueDay: Int, // 1 - 31

    @ColumnInfo(name = "interest_rate_ea")
    val interestRateEa: Double, // Tasa Efectiva Anual (ej: 28.5)

    @ColumnInfo(name = "handling_fee_cents")
    val handlingFeeCents: Long = 0L, // Cuota de manejo mensual

    @ColumnInfo(name = "last_four_digits")
    val lastFourDigits: String? = null,

    @ColumnInfo(name = "revolving_rate")
    val revolvingRate: Double = 1.0, // Porcentaje de compras a 1 cuota que entra en el mínimo (ej: 0.05 o 1.0)

    @ColumnInfo(name = "monthly_insurance_cents")
    val monthlyInsuranceCents: Long = 0L, // Seguro de vida deudor mensual

    @ColumnInfo(name = "installment_type")
    val installmentType: Int = 0 // 0: Tradicional (Cuota Fija), 1: Fintech (Capital Constante + Interés)
)
