package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.AllocationModelEntity
import com.personalfinance.cuentas.data.local.entities.BudgetEntity
import com.personalfinance.cuentas.data.local.entities.IncomeAllocationEntity
import com.personalfinance.cuentas.data.local.relations.AllocationModelWithBlocks
import kotlinx.coroutines.flow.Flow

@Dao
interface AllocationDao {

    @Transaction
    @Query("SELECT * FROM allocation_models WHERE is_active = 1 LIMIT 1")
    fun getActiveModelWithBlocks(): Flow<AllocationModelWithBlocks?>

    @Transaction
    @Query("SELECT * FROM allocation_models WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveModelWithBlocksSync(): AllocationModelWithBlocks?

    @Query("SELECT * FROM allocation_models WHERE is_active = 1 LIMIT 1")
    fun getActiveModel(): Flow<AllocationModelEntity?>

    @Transaction
    @Query("SELECT * FROM allocation_models ORDER BY id ASC")
    fun getAllModelsWithBlocks(): Flow<List<AllocationModelWithBlocks>>

    @Query("SELECT * FROM allocation_blocks WHERE model_id = :modelId ORDER BY order_index ASC")
    fun getBlocksForModel(modelId: Long): Flow<List<AllocationBlockEntity>>

    @Query("SELECT * FROM allocation_blocks WHERE id = :blockId")
    suspend fun getBlockById(blockId: Long): AllocationBlockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: AllocationModelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: AllocationBlockEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocks(blocks: List<AllocationBlockEntity>)

    @Query("UPDATE allocation_models SET is_active = 0")
    suspend fun deactivateAllModels()

    @Query("UPDATE allocation_models SET is_active = 1 WHERE id = :modelId")
    suspend fun activateModel(modelId: Long)

    @Transaction
    suspend fun setActiveModel(modelId: Long) {
        deactivateAllModels()
        activateModel(modelId)
    }

    @Query("SELECT COUNT(*) FROM allocation_models")
    suspend fun getModelCount(): Int

    // Sobres y Presupuestos
    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<BudgetEntity>)

    @Query("DELETE FROM budgets WHERE block_id = :blockId AND month = :month AND year = :year")
    suspend fun deleteBudgetForBlock(blockId: Long, month: Int, year: Int)

    // Repartición de ingresos (IncomeAllocations)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomeAllocations(allocations: List<IncomeAllocationEntity>)

    @Query("SELECT * FROM income_allocations WHERE income_id = :incomeId")
    suspend fun getAllocationsForIncome(incomeId: Long): List<IncomeAllocationEntity>

    @Query("""
        SELECT COALESCE(SUM(ia.assigned_cents), 0)
        FROM income_allocations ia
        INNER JOIN incomes i ON ia.income_id = i.id
        WHERE ia.block_id = :blockId AND i.is_received = 1
          AND i.received_date_millis BETWEEN :startMillis AND :endMillis
    """)
    fun getTotalAllocatedToBlockBetween(blockId: Long, startMillis: Long, endMillis: Long): Flow<Long>
}
