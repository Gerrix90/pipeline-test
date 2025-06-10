package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.domain.repository.EventRepositoryInterface
import com.jahi.pipelinetest.domain.repository.TaskRepositoryInterface
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val eventRepository: EventRepositoryInterface,
    private val taskRepository: TaskRepositoryInterface
) {

    suspend operator fun invoke(eventId: Int) {
        // Delete all tasks associated with this event first
        taskRepository.deleteTasksForEvent(eventId)
        // Then delete the event
        eventRepository.deleteEvent(eventId)
    }
}