package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringItemDao {

    @Query("SELECT * FROM recurring_items WHERE is_active = 1 ORDER BY next_due_date_millis ASC")
    fun getAllActiveRecurringItems(): Flow<List<RecurringItemEntity>>

    @Query("SELECT * FROM recurring_items WHERE is_active = 1 ORDER BY next_due_date_millis ASC")
    suspend fun getAllActiveRecurringItemsSync(): List<RecurringItemEntity>

    @Query("SELECT * FROM recurring_items ORDER BY id ASC")
    fun getAllRecurringItems(): Flow<List<RecurringItemEntity>>

    @Query("SELECT * FROM recurring_items ORDER BY id ASC")
    suspend fun getAllRecurringItemsSync(): List<RecurringItemEntity>

    @Query("SELECT * FROM recurring_items WHERE id = :id")
    fun getRecurringItemById(id: Long): Flow<RecurringItemEntity?>

    @Query("SELECT COALESCE(SUM(amount_cents), 0) FROM recurring_items WHERE is_active = 1")
    fun getTotalMonthlyRecurringCents(): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount_cents), 0) FROM recurring_items WHERE is_active = 1")
    suspend fun getTotalMonthlyRecurringCentsSync(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringItem(item: RecurringItemEntity): Long

    @Update
    suspend fun updateRecurringItem(item: RecurringItemEntity)

    @Delete
    suspend fun deleteRecurringItem(item: RecurringItemEntity)

    @Query("UPDATE recurring_items SET next_due_date_millis = :nextDueDate WHERE id = :id")
    suspend fun updateNextDueDate(id: Long, nextDueDate: Long)

    @Query("SELECT COUNT(*) FROM recurring_items")
    suspend fun getRecurringCount(): Int
}
