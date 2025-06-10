package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
import javax.inject.Inject

class ToggleTaskCompletionUseCase @Inject constructor(private val taskRepository: TaskRepositoryInterface) {
    
    suspend operator fun invoke(taskId: Int) {
        // Get task, toggle completion, and update
        val task = taskRepository.getTaskById(taskId)
        if (task != null) {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            taskRepository.updateTask(updatedTask)
        }
    }
}