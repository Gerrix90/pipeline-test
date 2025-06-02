package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.repository.TaskRepository

class GetTasksForEventUseCase(private val taskRepository: TaskRepository) {
    
    operator fun invoke(eventId: Int): List<Task> {
        return taskRepository.getTasksForEvent(eventId)
    }
}