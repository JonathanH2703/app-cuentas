package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts WHERE is_archived = 0 ORDER BY id ASC")
    fun getAllActiveAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE is_archived = 0 ORDER BY id ASC")
    suspend fun getAllActiveAccountsSync(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccountById(id: Long): Flow<AccountEntity?>

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountByIdSync(id: Long): AccountEntity?

    @Query("SELECT COALESCE(SUM(current_balance_cents), 0) FROM accounts WHERE is_archived = 0 AND include_in_total = 1")
    fun getTotalConsolidatedBalanceCents(): Flow<Long>

    @Query("SELECT COALESCE(SUM(current_balance_cents), 0) FROM accounts WHERE is_archived = 0 AND include_in_total = 1 AND is_static_saving = 0")
    fun getSpendableBalanceCents(): Flow<Long>

    @Query("SELECT COALESCE(SUM(current_balance_cents), 0) FROM accounts WHERE is_archived = 0 AND include_in_total = 1 AND is_static_saving = 1")
    fun getStaticSavingsBalanceCents(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity): Long

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("UPDATE accounts SET current_balance_cents = :newBalanceCents WHERE id = :id")
    suspend fun updateBalance(id: Long, newBalanceCents: Long)

    @Query("UPDATE accounts SET include_in_total = :include WHERE id = :id")
    suspend fun updateIncludeInTotal(id: Long, include: Boolean)

    @Query("UPDATE accounts SET is_static_saving = :isStatic WHERE id = :id")
    suspend fun updateStaticSaving(id: Long, isStatic: Boolean)

    @Query("UPDATE accounts SET is_exempt_from_gmf = :isExempt WHERE id = :id")
    suspend fun updateExemptFromGmf(id: Long, isExempt: Boolean)

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getAccountCount(): Int

    @Query("SELECT current_balance_cents FROM accounts WHERE id = :accountId LIMIT 1")
    fun getBalanceCents(accountId: Long): Flow<Long>
}