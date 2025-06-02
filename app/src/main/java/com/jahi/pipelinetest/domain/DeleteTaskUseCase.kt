package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.repository.TaskRepository

class DeleteTaskUseCase(private val taskRepository: TaskRepository) {
    
    operator fun invoke(taskId: Int) {
        taskRepository.deleteTask(taskId)
    }
}