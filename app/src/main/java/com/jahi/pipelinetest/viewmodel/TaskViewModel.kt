package com.jahi.pipelinetest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.jahi.pipelinetest.domain.*
import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.repository.TaskRepository
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
            .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, emptyList())

    var isAddingTask by mutableStateOf(false)
        private set
    
    var newTaskDescription by mutableStateOf("")
        private set

    fun loadTasksForEvent(eventId: Int) {
        viewModelScope.launch {
            _tasks.value = getTasksForEventUseCase(eventId)
        }
    }

    fun addTask(eventId: Int, description: String) {
        viewModelScope.launch {
            addTaskToEventUseCase(eventId, description)
            loadTasksForEvent(eventId)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task)
            loadTasksForEvent(task.eventId)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task.id)
            loadTasksForEvent(task.eventId)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            toggleTaskCompletionUseCase(task.id)
            loadTasksForEvent(task.eventId)
        }
    }

    fun startAddingTask() {
        isAddingTask = true
    }

    fun cancelAddingTask() {
        isAddingTask = false
        newTaskDescription = ""
    }

    fun updateNewTaskDescription(description: String) {
        newTaskDescription = description
    }

    fun confirmAddTask(eventId: Int) {
        if (newTaskDescription.isNotBlank()) {
            addTask(eventId, newTaskDescription)
            cancelAddingTask()
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