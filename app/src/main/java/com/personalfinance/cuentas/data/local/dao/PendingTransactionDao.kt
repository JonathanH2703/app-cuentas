package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personalfinance.cuentas.data.local.entities.PendingStatus
import com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingTransactionDao {

    @Query("SELECT * FROM pending_transactions WHERE status = :status ORDER BY detected_date_millis DESC")
    fun getTransactionsByStatus(status: PendingStatus): Flow<List<PendingTransactionEntity>>

    @Query("SELECT * FROM pending_transactions ORDER BY detected_date_millis DESC LIMIT :limit")
    fun getRecentCaptures(limit: Int = 50): Flow<List<PendingTransactionEntity>>

    @Query("SELECT COUNT(*) FROM pending_transactions WHERE status = 'PENDING'")
    fun getPendingCount(): Flow<Int>

    // Pagos de obligación (tarjeta de crédito o crédito/préstamo) detectados y pendientes de
    // confirmación por el usuario.
    @Query("""
        SELECT * FROM pending_transactions 
        WHERE status = 'PENDING' 
          AND (suggested_card_id IS NOT NULL OR suggested_debt_id IS NOT NULL)
        ORDER BY detected_date_millis DESC
    """)
    fun getPendingObligationPayments(): Flow<List<PendingTransactionEntity>>

    @Query("SELECT * FROM pending_transactions WHERE deduplication_hash = :hash LIMIT 1")
    suspend fun findByDeduplicationHash(hash: String): PendingTransactionEntity?

    @Query("SELECT * FROM pending_transactions WHERE deduplication_hash = :hash AND detected_date_millis >= :sinceMillis LIMIT 1")
    suspend fun findRecentByHash(hash: String, sinceMillis: Long): PendingTransactionEntity?

    @Query("""
        SELECT * FROM pending_transactions 
        WHERE suggested_amount_cents = :amountCents 
          AND (source_sender = :sender OR source_sender LIKE '%' || :sender || '%')
          AND detected_date_millis >= :sinceMillis 
        LIMIT 1
    """)
    suspend fun findRecentSimilarCapture(
        amountCents: Long,
        sender: String,
        sinceMillis: Long
    ): PendingTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pendingTransaction: PendingTransactionEntity): Long

    @Update
    suspend fun update(pendingTransaction: PendingTransactionEntity)

    @Query("UPDATE pending_transactions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: PendingStatus)

    @Query("DELETE FROM pending_transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM pending_transactions WHERE suggested_amount_cents = :amountCents AND (suggested_account_id = :accountId OR :accountId = 0)")
    suspend fun deleteSimilarCapture(amountCents: Long, accountId: Long)

    @Query("DELETE FROM pending_transactions WHERE deduplication_hash = :hash")
    suspend fun deleteByDeduplicationHash(hash: String)
}
