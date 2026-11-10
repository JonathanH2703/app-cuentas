package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class PendingSource {
    NOTIFICATION,
    SMS,
    EMAIL_NOTIFICATION
}

enum class PendingStatus {
    PENDING,
    CONFIRMED,
    DISCARDED,
    AUTO_CONFIRMED
}

@Entity(
    tableName = "pending_transactions",
    indices = [
        Index(value = ["deduplication_hash"]),
        Index(value = ["status", "detected_date_millis"])
    ]
)
data class PendingTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "source")
    val source: PendingSource,

    @ColumnInfo(name = "source_sender")
    val sourceSender: String,

    @ColumnInfo(name = "raw_text")
    val rawText: String? = null,

    @ColumnInfo(name = "deduplication_hash")
    val deduplicationHash: String,

    @ColumnInfo(name = "suggested_amount_cents")
    val suggestedAmountCents: Long,

    @ColumnInfo(name = "suggested_merchant")
    val suggestedMerchant: String? = null,

    @ColumnInfo(name = "suggested_account_id")
    val suggestedAccountId: Long? = null,

    @ColumnInfo(name = "suggested_category_id")
    val suggestedCategoryId: Long? = null,

    @ColumnInfo(name = "suggested_block_id")
    val suggestedBlockId: Long? = null,

    @ColumnInfo(name = "suggested_type")
    val suggestedType: TransactionType = TransactionType.EXPENSE,

    // Si no es nulo, la captura corresponde al pago de una tarjeta de crédito y guarda el ID
    // de la tarjeta a la que se debe aplicar (bajar cupo usado). Se usa para la confirmación.
    @ColumnInfo(name = "suggested_card_id")
    val suggestedCardId: Long? = null,

    // Si no es nulo, la captura corresponde al pago de un crédito/préstamo y guarda el ID
    // del crédito al que se debe aplicar (reducir saldo). Se usa para la confirmación.
    @ColumnInfo(name = "suggested_debt_id")
    val suggestedDebtId: Long? = null,

    @ColumnInfo(name = "detected_date_millis")
    val detectedDateMillis: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "status")
    val status: PendingStatus = PendingStatus.PENDING
)
