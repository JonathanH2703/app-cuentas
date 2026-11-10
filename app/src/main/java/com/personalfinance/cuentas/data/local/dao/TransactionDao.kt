package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import com.personalfinance.cuentas.data.local.relations.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date_millis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date_millis DESC")
    fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date_millis DESC LIMIT :limit")
    fun getRecentTransactionsWithDetails(limit: Int): Flow<List<TransactionWithDetails>>

    @Transaction
    @Query("""
        SELECT * FROM transactions 
        WHERE (:accountId IS NULL OR account_id = :accountId)
          AND (:categoryId IS NULL OR category_id = :categoryId)
          AND (:type IS NULL OR type = :type)
        ORDER BY date_millis DESC
    """)
    fun getFilteredTransactions(accountId: Long?, categoryId: Long?, type: String?): Flow<List<TransactionWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("""
        UPDATE transactions 
        SET linked_debt_id = :debtId, linked_recurring_item_id = :recurringItemId 
        WHERE id = :transactionId
    """)
    suspend fun updateAnchor(transactionId: Long, debtId: Long?, recurringItemId: Long?)

    @Query("""
        UPDATE transactions
        SET category_id = :categoryId, block_id = :blockId, amount_cents = :amountCents, note = :note, is_leisure = :isLeisure
        WHERE id = :transactionId
    """)
    suspend fun updateTransactionDetails(
        transactionId: Long,
        categoryId: Long,
        blockId: Long?,
        amountCents: Long,
        note: String?,
        isLeisure: Boolean
    )

    @Query("UPDATE transactions SET type = :type WHERE id = :transactionId")
    suspend fun updateTransactionType(transactionId: Long, type: TransactionType)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    /**
     * Ancla (o desancla) un movimiento a una cuenta por cobrar ("Me deben").
     * Al usarse para saldar, el ingreso real que representa el pago queda vinculado a la deuda.
     */
    @Query("UPDATE transactions SET linked_receivable_id = :receivableId WHERE id = :transactionId")
    suspend fun updateReceivableAnchor(transactionId: Long, receivableId: Long?)

    /** Ingresos reales que aún no están anclados a ninguna cuenta por cobrar (candidatos a anclar). */
    @Query("""
        SELECT * FROM transactions 
        WHERE type = 'INCOME' AND linked_receivable_id IS NULL 
        ORDER BY date_millis DESC 
        LIMIT :limit
    """)
    fun getUnanchoredIncomeTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions 
        WHERE account_id = :accountId 
          AND amount_cents = :amountCents 
          AND type = :type 
          AND date_millis >= :sinceMillis 
        LIMIT 1
    """)
    suspend fun findRecentSimilarTransaction(
        accountId: Long,
        amountCents: Long,
        type: TransactionType,
        sinceMillis: Long
    ): TransactionEntity?

    @Query("""
        SELECT COALESCE(SUM(amount_cents), 0) FROM transactions 
        WHERE type = 'EXPENSE' AND date_millis BETWEEN :startMillis AND :endMillis
    """)
    fun getTotalExpensesBetween(startMillis: Long, endMillis: Long): Flow<Long>

    @Query("""
        SELECT COALESCE(SUM(amount_cents), 0) FROM transactions 
        WHERE block_id = :blockId AND type = 'EXPENSE' AND date_millis BETWEEN :startMillis AND :endMillis
    """)
    fun getTotalSpentInBlockBetween(blockId: Long, startMillis: Long, endMillis: Long): Flow<Long>

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int
}
