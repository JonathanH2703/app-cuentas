package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CapturePatternType {
    NOTIFICATION_APP,
    SMS
}

@Entity(tableName = "capture_rules")
data class CaptureRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "sender_filter")
    val senderFilter: String, // e.g. "com.bancolombia.personas" o remitente SMS "890099"

    @ColumnInfo(name = "pattern_type")
    val patternType: CapturePatternType,

    @ColumnInfo(name = "regex_pattern")
    val regexPattern: String,

    @ColumnInfo(name = "extracted_type")
    val extractedType: TransactionType = TransactionType.EXPENSE,

    @ColumnInfo(name = "default_account_id")
    val defaultAccountId: Long? = null,

    @ColumnInfo(name = "confidence_score")
    val confidenceScore: Float = 0.9f,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
