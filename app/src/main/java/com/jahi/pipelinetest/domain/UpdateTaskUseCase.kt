package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepositoryInterface
) {
    
    suspend operator fun invoke(task: Task) {
        taskRepository.updateTask(task)
    }
}