package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.personalfinance.cuentas.data.local.entities.ReceivableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceivableDao {

    @Query("SELECT * FROM receivables WHERE is_settled = 0 ORDER BY created_at DESC")
    fun getPendingReceivables(): Flow<List<ReceivableEntity>>

    @Query("SELECT * FROM receivables WHERE is_settled = 1 ORDER BY settled_at_millis DESC")
    fun getSettledReceivables(): Flow<List<ReceivableEntity>>

    @Query("SELECT * FROM receivables ORDER BY created_at DESC")
    fun getAllReceivables(): Flow<List<ReceivableEntity>>

    @Query("SELECT * FROM receivables WHERE source_transaction_id = :transactionId")
    suspend fun getReceivablesForTransaction(transactionId: Long): List<ReceivableEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceivable(receivable: ReceivableEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceivables(receivables: List<ReceivableEntity>)

    @Delete
    suspend fun deleteReceivable(receivable: ReceivableEntity)

    @Query("""
        UPDATE receivables
        SET is_settled = 1,
            settled_at_millis = :settledAtMillis,
            settled_account_id = :accountId,
            settled_transaction_id = :transactionId
        WHERE id = :id
    """)
    suspend fun markSettled(id: Long, settledAtMillis: Long, accountId: Long, transactionId: Long?)
}
