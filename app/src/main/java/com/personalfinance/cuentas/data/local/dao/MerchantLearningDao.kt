package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personalfinance.cuentas.data.local.entities.MerchantLearningEntity

@Dao
interface MerchantLearningDao {

    @Query("SELECT * FROM merchant_learnings WHERE normalized_merchant = :normalizedMerchant LIMIT 1")
    suspend fun getLearningForMerchant(normalizedMerchant: String): MerchantLearningEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(learning: MerchantLearningEntity): Long

    @Query("UPDATE merchant_learnings SET hit_count = hit_count + 1 WHERE normalized_merchant = :normalizedMerchant")
    suspend fun incrementHitCount(normalizedMerchant: String)
}
