package com.jahi.pipelinetest.domain.repository

import com.jahi.pipelinetest.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepositoryInterface {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksForEvent(eventId: Int): Flow<List<Task>>
    suspend fun getTaskById(taskId: Int): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: Int)
    suspend fun deleteTasksForEvent(eventId: Int)
    suspend fun generateTaskId(): Int
    suspend fun getTaskCount(): Int
}