package com.jahi.pipelinetest.data.local.dao

import androidx.room.*
import com.jahi.pipelinetest.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE eventId = :eventId ORDER BY id ASC")
    fun getTasksForEvent(eventId: Int): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
    
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
    
    @Query("DELETE FROM tasks WHERE eventId = :eventId")
    suspend fun deleteTasksForEvent(eventId: Int)
    
    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int
    
    @Query("SELECT MAX(id) FROM tasks")
    suspend fun getMaxTaskId(): Int?
}