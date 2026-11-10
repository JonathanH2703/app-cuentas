package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "card_purchases",
    foreignKeys = [
        ForeignKey(
            entity = CreditCardEntity::class,
            parentColumns = ["id"],
            childColumns = ["card_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["card_id"]),
        Index(value = ["card_id", "is_fully_paid"])
    ]
)
data class CardPurchaseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "card_id")
    val cardId: Long,

    @ColumnInfo(name = "merchant")
    val merchant: String,

    @ColumnInfo(name = "purchase_date_millis")
    val purchaseDateMillis: Long,

    @ColumnInfo(name = "total_amount_cents")
    val totalAmountCents: Long,

    @ColumnInfo(name = "installments_total")
    val installmentsTotal: Int = 1,

    @ColumnInfo(name = "installments_paid")
    val installmentsPaid: Int = 0,

    @ColumnInfo(name = "interest_rate_nm")
    val interestRateNm: Double = 0.0, // Nominal mensual

    @ColumnInfo(name = "is_fully_paid")
    val isFullyPaid: Boolean = false
)
