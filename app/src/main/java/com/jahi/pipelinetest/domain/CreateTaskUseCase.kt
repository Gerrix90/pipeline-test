package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepositoryInterface
) {

    suspend operator fun invoke(eventId: Int, description: String, dueDate: String? = null) {
        val task = Task(
            id = taskRepository.generateTaskId(),
            eventId = eventId,
            description = description,
            dueDate = dueDate
        )
        taskRepository.insertTask(task)
    }
}
