package com.jahi.pipelinetest.repository

import com.jahi.pipelinetest.Prefs
import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
import com.jahi.pipelinetest.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskRepository(private val prefs: Prefs) : TaskRepositoryInterface {

    private val _tasks = MutableStateFlow(prefs.tasks)
    val tasks: Flow<List<Task>> = _tasks.asStateFlow()

    override fun getAllTasks(): Flow<List<Task>> = _tasks.asStateFlow()

    override suspend fun getTaskById(taskId: Int): Task? {
        return prefs.tasks.find { it.id == taskId }
    }

    override suspend fun getTaskCount(): Int {
        return prefs.tasks.size
    }

    init {
        val maxId = prefs.tasks.maxOfOrNull { it.id } ?: 0
        if (prefs.nextTaskId <= maxId) {
            prefs.nextTaskId = maxId + 1
        }
    }

    override suspend fun generateTaskId(): Int {
        val id = prefs.nextTaskId
        prefs.nextTaskId = id + 1
        return id
    }
    
    private fun refreshTasks() {
        _tasks.value = prefs.tasks
    }
    
    private fun saveTasks(tasks: List<Task>) {
        prefs.tasks = tasks.toMutableList()
        _tasks.value = tasks.toMutableList()
    }
    
    override suspend fun insertTask(task: Task) {
        val currentTasks = prefs.tasks.toMutableList()
        currentTasks.add(task)
        saveTasks(currentTasks)
    }
    
    override suspend fun updateTask(task: Task) {
        val currentTasks = prefs.tasks.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentTasks[index] = task
            saveTasks(currentTasks)
        }
    }
    
    override suspend fun deleteTask(taskId: Int) {
        val currentTasks = prefs.tasks.toMutableList()
        currentTasks.removeAll { it.id == taskId }
        saveTasks(currentTasks)
    }
    
    override fun getTasksForEvent(eventId: Int): Flow<List<Task>> {
        return MutableStateFlow(prefs.tasks.filter { it.eventId == eventId }).asStateFlow()
    }
    
    private fun toggleTaskCompletion(taskId: Int) {
        val currentTasks = prefs.tasks.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            currentTasks[index] = currentTasks[index].copy(isCompleted = !currentTasks[index].isCompleted)
            saveTasks(currentTasks)
        }
    }
    
    override suspend fun deleteTasksForEvent(eventId: Int) {
        val currentTasks = prefs.tasks.toMutableList()
        currentTasks.removeAll { it.eventId == eventId }
        saveTasks(currentTasks)
    }
}