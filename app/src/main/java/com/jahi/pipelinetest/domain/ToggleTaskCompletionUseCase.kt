package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.repository.TaskRepository

class ToggleTaskCompletionUseCase(private val taskRepository: TaskRepository) {
    
    operator fun invoke(taskId: Int) {
        taskRepository.toggleTaskCompletion(taskId)
    }
}