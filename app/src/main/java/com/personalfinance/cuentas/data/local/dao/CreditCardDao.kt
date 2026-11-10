package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditCardDao {

    @Transaction
    @Query("SELECT * FROM credit_cards ORDER BY id ASC")
    fun getAllCreditCardsWithAccount(): Flow<List<CreditCardWithAccount>>

    @Transaction
    @Query("SELECT * FROM credit_cards ORDER BY id ASC")
    suspend fun getAllCreditCardsWithAccountSync(): List<CreditCardWithAccount>

    @Transaction
    @Query("SELECT * FROM credit_cards WHERE id = :id")
    fun getCreditCardById(id: Long): Flow<CreditCardWithAccount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditCard(card: CreditCardEntity): Long

    @Update
    suspend fun updateCreditCard(card: CreditCardEntity)

    @Delete
    suspend fun deleteCreditCard(card: CreditCardEntity)

    @Query("SELECT * FROM card_purchases WHERE card_id = :cardId ORDER BY purchase_date_millis DESC")
    fun getPurchasesForCard(cardId: Long): Flow<List<CardPurchaseEntity>>

    @Query("SELECT * FROM card_purchases WHERE card_id = :cardId AND is_fully_paid = 0 ORDER BY purchase_date_millis DESC")
    fun getActivePurchasesForCard(cardId: Long): Flow<List<CardPurchaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: CardPurchaseEntity): Long

    @Update
    suspend fun updatePurchase(purchase: CardPurchaseEntity)

    @Delete
    suspend fun deletePurchase(purchase: CardPurchaseEntity)

    @Query("SELECT COALESCE(SUM(total_amount_cents), 0) FROM card_purchases WHERE is_fully_paid = 0")
    fun getTotalUsedCreditCents(): Flow<Long>

    @Query("SELECT COUNT(*) FROM credit_cards")
    suspend fun getCardCount(): Int

    @Query("""
        SELECT COALESCE(SUM(total_amount_cents), 0)
        FROM card_purchases
        WHERE card_id = :cardId AND is_fully_paid = 0
    """)
    fun getPendingCycleCents(cardId: Long): Flow<Long>
}