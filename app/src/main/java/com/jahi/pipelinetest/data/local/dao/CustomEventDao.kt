package com.jahi.pipelinetest.data.local.dao

import androidx.room.*
import com.jahi.pipelinetest.data.local.entities.CustomEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomEventDao {
    
    @Query("SELECT * FROM custom_events ORDER BY id ASC")
    fun getAllEvents(): Flow<List<CustomEventEntity>>
    
    @Query("SELECT * FROM custom_events WHERE id = :eventId")
    suspend fun getEventById(eventId: Int): CustomEventEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CustomEventEntity)
    
    @Update
    suspend fun updateEvent(event: CustomEventEntity)
    
    @Delete
    suspend fun deleteEvent(event: CustomEventEntity)
    
    @Query("DELETE FROM custom_events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Int)
    
    @Query("SELECT COUNT(*) FROM custom_events")
    suspend fun getEventCount(): Int
}