package com.jahi.pipelinetest.data.repository

import com.jahi.pipelinetest.data.local.dao.TaskDao
import com.jahi.pipelinetest.data.mappers.toDomainModel
import com.jahi.pipelinetest.data.mappers.toDomainModels
import com.jahi.pipelinetest.data.mappers.toEntity
import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
import com.jahi.pipelinetest.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepositoryInterface {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getTasksForEvent(eventId: Int): Flow<List<Task>> {
        return taskDao.getTasksForEvent(eventId).map { entities ->
            entities.toDomainModels()
        }
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)?.toDomainModel()
    }

    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }

    override suspend fun deleteTasksForEvent(eventId: Int) {
        taskDao.deleteTasksForEvent(eventId)
    }

    override suspend fun generateTaskId(): Int {
        val maxId = taskDao.getMaxTaskId() ?: 0
        return maxId + 1
    }

    override suspend fun getTaskCount(): Int {
        return taskDao.getTaskCount()
    }
}