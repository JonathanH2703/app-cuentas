package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class IncomeFrequency {
    ONE_TIME,   // Ingreso único / extraordinario
    WEEKLY,     // Semanal
    BIWEEKLY,   // Quincenal
    MONTHLY,    // Mensual
    CUSTOM      // Personalizado
}

@Entity(
    tableName = "incomes",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["account_id"]),
        Index(value = ["expected_date_millis"]),
        Index(value = ["is_received"])
    ]
)
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "account_id")
    val accountId: Long,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "amount_cents")
    val amountCents: Long,

    @ColumnInfo(name = "frequency")
    val frequency: IncomeFrequency = IncomeFrequency.MONTHLY,

    @ColumnInfo(name = "expected_date_millis")
    val expectedDateMillis: Long,

    @ColumnInfo(name = "received_date_millis")
    val receivedDateMillis: Long? = null,

    @ColumnInfo(name = "is_received")
    val isReceived: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
