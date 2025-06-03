package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.repository.TaskRepository

class CreateTaskUseCase(private val taskRepository: TaskRepository) {

    operator fun invoke(eventId: Int, description: String, dueDate: String? = null) {
        val task = Task(
            id = taskRepository.generateTaskId(),
            eventId = eventId,
            description = description,
            dueDate = dueDate
        )
        taskRepository.addTask(task)
    }
}
