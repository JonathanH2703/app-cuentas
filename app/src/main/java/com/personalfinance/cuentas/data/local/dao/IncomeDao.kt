package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personalfinance.cuentas.data.local.entities.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Query("SELECT * FROM incomes ORDER BY expected_date_millis ASC")
    fun getAllIncomes(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE is_received = 0 ORDER BY expected_date_millis ASC")
    fun getPendingIncomes(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE is_received = 1 ORDER BY received_date_millis DESC")
    fun getReceivedIncomes(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE id = :id")
    suspend fun getIncomeById(id: Long): IncomeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity): Long

    @Update
    suspend fun updateIncome(income: IncomeEntity)

    @Delete
    suspend fun deleteIncome(income: IncomeEntity)

    @Query("UPDATE incomes SET is_received = 1, received_date_millis = :receivedMillis WHERE id = :id")
    suspend fun markAsReceived(id: Long, receivedMillis: Long)

    @Query("SELECT COALESCE(SUM(amount_cents), 0) FROM incomes WHERE is_received = 0")
    fun getTotalPendingIncomesCents(): Flow<Long>
}
