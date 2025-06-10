package com.jahi.pipelinetest.data.repository

import com.jahi.pipelinetest.data.local.dao.CustomEventDao
import com.jahi.pipelinetest.data.mappers.toDomainModel
import com.jahi.pipelinetest.data.mappers.toDomainModels
import com.jahi.pipelinetest.data.mappers.toEntity
import com.jahi.pipelinetest.domain.repository.EventRepositoryInterface
import com.jahi.pipelinetest.model.CustomEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: CustomEventDao
) : EventRepositoryInterface {

    override fun getAllEvents(): Flow<List<CustomEvent>> {
        return eventDao.getAllEvents().map { entities ->
            entities.toDomainModels()
        }
    }

    override suspend fun getEventById(eventId: Int): CustomEvent? {
        return eventDao.getEventById(eventId)?.toDomainModel()
    }

    override suspend fun insertEvent(event: CustomEvent) {
        eventDao.insertEvent(event.toEntity())
    }

    override suspend fun updateEvent(event: CustomEvent) {
        eventDao.updateEvent(event.toEntity())
    }

    override suspend fun deleteEvent(eventId: Int) {
        eventDao.deleteEventById(eventId)
    }

    override suspend fun getEventCount(): Int {
        return eventDao.getEventCount()
    }
}