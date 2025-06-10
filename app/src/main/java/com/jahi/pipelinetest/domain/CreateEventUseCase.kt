package com.jahi.pipelinetest.domain

import com.jahi.pipelinetest.model.CustomEvent
import com.jahi.pipelinetest.domain.repository.EventRepositoryInterface
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: EventRepositoryInterface
) {

    suspend operator fun invoke(name: String, date: String, showTime: Boolean = false, showInWidget: Boolean = false) {
        val event = CustomEvent(
            name = name,
            date = date,
            showTime = showTime,
            showInWidget = showInWidget
        )
        eventRepository.insertEvent(event)
    }
}