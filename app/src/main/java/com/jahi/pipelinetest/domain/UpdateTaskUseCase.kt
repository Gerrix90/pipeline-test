package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.repository.TaskRepository

class UpdateTaskUseCase(private val taskRepository: TaskRepository) {
    
    operator fun invoke(task: Task) {
        taskRepository.updateTask(task)
    }
}