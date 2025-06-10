package com.jahi.pipelinetest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jahi.pipelinetest.domain.*
import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
import com.jahi.pipelinetest.model.Task
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val taskRepository: TaskRepositoryInterface
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    val allTasks: StateFlow<List<Task>> =
        taskRepository.getAllTasks()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    fun loadTasksForEvent(eventId: Int) {
        viewModelScope.launch {
            getTasksUseCase(eventId).collect { tasks ->
                _tasks.value = tasks
            }
        }
    }

    fun addTask(eventId: Int, description: String, dueDate: String?) {
        viewModelScope.launch {
            createTaskUseCase(eventId, description, dueDate)
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

}