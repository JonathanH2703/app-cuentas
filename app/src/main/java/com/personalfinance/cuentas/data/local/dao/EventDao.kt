package com.personalfinance.cuentas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personalfinance.cuentas.data.local.entities.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events WHERE is_active = 1 ORDER BY month_of_year ASC, day_of_month ASC")
    fun getAllActiveEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY month_of_year ASC, day_of_month ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE is_active = 1 ORDER BY month_of_year ASC, day_of_month ASC")
    suspend fun getAllActiveEventsSync(): List<EventEntity>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventByIdSync(id: Long): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventCount(): Int
}
