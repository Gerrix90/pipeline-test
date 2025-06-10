package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.model.CustomEvent
import com.jahi.pipelinetest.domain.repository.EventRepositoryInterface
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(
    private val eventRepository: EventRepositoryInterface
) {

    suspend operator fun invoke(event: CustomEvent) {
        eventRepository.updateEvent(event)
    }
}