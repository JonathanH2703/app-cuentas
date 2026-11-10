package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.entities.DebtPaymentEntity
import com.personalfinance.cuentas.data.local.relations.DebtWithPayments
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    @Transaction
    @Query("SELECT * FROM debts ORDER BY is_settled ASC, current_balance_cents DESC")
    fun getAllDebtsWithPayments(): Flow<List<DebtWithPayments>>

    @Query("SELECT * FROM debts WHERE is_settled = 0 ORDER BY current_balance_cents DESC")
    fun getActiveDebts(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE is_settled = 0 ORDER BY current_balance_cents DESC")
    suspend fun getActiveDebtsSync(): List<DebtEntity>

    @Query("SELECT * FROM debts WHERE id = :id")
    fun getDebtById(id: Long): Flow<DebtEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    @Query("SELECT * FROM debt_payments WHERE debt_id = :debtId ORDER BY payment_date_millis DESC")
    fun getPaymentsForDebt(debtId: Long): Flow<List<DebtPaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: DebtPaymentEntity): Long

    @Delete
    suspend fun deletePayment(payment: DebtPaymentEntity)

    /** Busca el pago exacto asociado a una transacción anclada, para poder revertirlo. */
    @Query("""
        SELECT * FROM debt_payments 
        WHERE debt_id = :debtId AND total_paid_cents = :amountCents AND payment_date_millis = :dateMillis 
        ORDER BY id DESC LIMIT 1
    """)
    suspend fun findPayment(debtId: Long, amountCents: Long, dateMillis: Long): DebtPaymentEntity?

    /** Devuelve al saldo el capital revertido al desanclar o eliminar una transacción. */
    @Query("UPDATE debts SET current_balance_cents = current_balance_cents + :principalCents, is_settled = 0 WHERE id = :debtId")
    suspend fun restoreBalance(debtId: Long, principalCents: Long)

    @Query("UPDATE debts SET next_due_date_millis = :nextDueDateMillis WHERE id = :debtId")
    suspend fun updateNextDueDate(debtId: Long, nextDueDateMillis: Long?)

    @Query("UPDATE debts SET current_balance_cents = :newBalanceCents, is_settled = :settled WHERE id = :debtId")
    suspend fun updateBalance(debtId: Long, newBalanceCents: Long, settled: Boolean)

    @Query("SELECT COALESCE(SUM(current_balance_cents), 0) FROM debts WHERE is_settled = 0")
    fun getTotalDebtBalanceCents(): Flow<Long>

    @Query("SELECT COUNT(*) FROM debts")
    suspend fun getDebtCount(): Int
}
