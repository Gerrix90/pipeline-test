package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.model.CustomEvent
import com.jahi.pipelinetest.domain.repository.EventRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventRepository: EventRepositoryInterface
) {

    operator fun invoke(): Flow<List<CustomEvent>> {
        return eventRepository.getAllEvents()
    }
    
    suspend fun getEventById(eventId: Int): CustomEvent? {
        return eventRepository.getEventById(eventId)
    }
}