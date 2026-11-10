package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "merchant_learnings",
    indices = [
        Index(value = ["normalized_merchant"], unique = true)
    ]
)
data class MerchantLearningEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "normalized_merchant")
    val normalizedMerchant: String,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "block_id")
    val blockId: Long,

    @ColumnInfo(name = "hit_count")
    val hitCount: Int = 1
)
