package com.jahi.pipelinetest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jahi.pipelinetest.domain.*
import android.content.Context
import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.repository.TaskRepository
import com.jahi.pipelinetest.scheduleTaskAlarms
import com.jahi.pipelinetest.cancelTaskAlarms
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class TaskViewModel(
    private val addTaskToEventUseCase: AddTaskToEventUseCase,
    private val getTasksForEventUseCase: GetTasksForEventUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val taskRepository: com.jahi.pipelinetest.repository.TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    val allTasks: StateFlow<List<Task>> =
        taskRepository.tasks
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private suspend fun rescheduleAlarms(context: Context) {
        val tasks = taskRepository.tasks.first()
        cancelTaskAlarms(context, tasks)
        scheduleTaskAlarms(context, tasks)
    }

    fun loadTasksForEvent(eventId: Int) {
        viewModelScope.launch {
            _tasks.value = getTasksForEventUseCase(eventId)
        }
    }

    fun addTask(context: Context, eventId: Int, description: String, dueDate: String?) {
        viewModelScope.launch {
            addTaskToEventUseCase(eventId, description, dueDate)
            loadTasksForEvent(eventId)
            rescheduleAlarms(context)
        }
    }

    fun updateTask(context: Context, task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task)
            loadTasksForEvent(task.eventId)
            rescheduleAlarms(context)
        }
    }

    fun deleteTask(context: Context, task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task.id)
            loadTasksForEvent(task.eventId)
            rescheduleAlarms(context)
        }
    }

    fun toggleTaskCompletion(context: Context, task: Task) {
        viewModelScope.launch {
            toggleTaskCompletionUseCase(task.id)
            loadTasksForEvent(task.eventId)
            rescheduleAlarms(context)
        }
    }

}

class TaskViewModelFactory(
    private val addTaskToEventUseCase: AddTaskToEventUseCase,
    private val getTasksForEventUseCase: GetTasksForEventUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val taskRepository: com.jahi.pipelinetest.repository.TaskRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(
                addTaskToEventUseCase,
                getTasksForEventUseCase,
                updateTaskUseCase,
                deleteTaskUseCase,
                toggleTaskCompletionUseCase,
                taskRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}