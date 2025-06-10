package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(private val taskRepository: TaskRepositoryInterface) {
    
    suspend operator fun invoke(taskId: Int) {
        taskRepository.deleteTask(taskId)
    }
}