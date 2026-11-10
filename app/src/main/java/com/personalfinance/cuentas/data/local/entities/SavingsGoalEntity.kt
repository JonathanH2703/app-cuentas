package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "target_amount_cents")
    val targetAmountCents: Long,

    @ColumnInfo(name = "current_saved_cents")
    val currentSavedCents: Long = 0L,

    @ColumnInfo(name = "target_date_millis")
    val targetDateMillis: Long? = null,

    @ColumnInfo(name = "is_emergency_fund")
    val isEmergencyFund: Boolean = false,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false
)
