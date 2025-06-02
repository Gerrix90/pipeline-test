package com.jahi.pipelinetest.repository

import com.jahi.pipelinetest.Prefs
import com.jahi.pipelinetest.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskRepository(private val prefs: Prefs) {
    
    private val _tasks = MutableStateFlow(prefs.tasks)
    val tasks: Flow<List<Task>> = _tasks.asStateFlow()
    
    private fun refreshTasks() {
        _tasks.value = prefs.tasks
    }
    
    private fun saveTasks(tasks: List<Task>) {
        prefs.tasks = tasks.toMutableList()
        _tasks.value = tasks.toMutableList()
    }
    
    fun addTask(task: Task) {
        val currentTasks = prefs.tasks.toMutableList()
        currentTasks.add(task)
        saveTasks(currentTasks)
    }
    
    fun updateTask(task: Task) {
        val currentTasks = prefs.tasks.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentTasks[index] = task
            saveTasks(currentTasks)
        }
    }
    
    fun deleteTask(taskId: Int) {
        val currentTasks = prefs.tasks.toMutableList()
        currentTasks.removeAll { it.id == taskId }
        saveTasks(currentTasks)
    }
    
    fun getTasksForEvent(eventId: Int): List<Task> {
        return prefs.tasks.filter { it.eventId == eventId }
    }
    
    fun toggleTaskCompletion(taskId: Int) {
        val currentTasks = prefs.tasks.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            currentTasks[index] = currentTasks[index].copy(isCompleted = !currentTasks[index].isCompleted)
            saveTasks(currentTasks)
        }
    }
    
    fun deleteTasksForEvent(eventId: Int) {
        val currentTasks = prefs.tasks.toMutableList()
        currentTasks.removeAll { it.eventId == eventId }
        saveTasks(currentTasks)
    }
}