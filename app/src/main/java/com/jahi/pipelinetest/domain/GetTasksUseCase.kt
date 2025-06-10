package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(private val taskRepository: TaskRepositoryInterface) {
    
    operator fun invoke(eventId: Int): Flow<List<Task>> {
        return taskRepository.getTasksForEvent(eventId)
    }
}