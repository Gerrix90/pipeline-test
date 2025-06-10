package com.jahi.pipelinetest.domain.repository

import com.jahi.pipelinetest.model.CustomEvent
import kotlinx.coroutines.flow.Flow

interface EventRepositoryInterface {
    fun getAllEvents(): Flow<List<CustomEvent>>
    suspend fun getEventById(eventId: Int): CustomEvent?
    suspend fun insertEvent(event: CustomEvent)
    suspend fun updateEvent(event: CustomEvent)
    suspend fun deleteEvent(eventId: Int)
    suspend fun getEventCount(): Int
}