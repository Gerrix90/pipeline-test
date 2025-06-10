package com.jahi.pipelinetest.repository

import com.jahi.pipelinetest.Prefs
import com.jahi.pipelinetest.model.CustomEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventRepository(private val prefs: Prefs) {

    private val _events = MutableStateFlow(prefs.customEvents)
    val events: Flow<List<CustomEvent>> = _events.asStateFlow()

    private fun refreshEvents() {
        _events.value = prefs.customEvents
    }
    
    private fun saveEvents(events: List<CustomEvent>) {
        prefs.customEvents = events.toMutableList()
        _events.value = events.toMutableList()
    }
    
    fun addEvent(event: CustomEvent) {
        val currentEvents = prefs.customEvents.toMutableList()
        currentEvents.add(event)
        saveEvents(currentEvents)
    }
    
    fun updateEvent(event: CustomEvent) {
        val currentEvents = prefs.customEvents.toMutableList()
        val index = currentEvents.indexOfFirst { it.id == event.id }
        if (index != -1) {
            currentEvents[index] = event
            saveEvents(currentEvents)
        }
    }
    
    fun deleteEvent(eventId: Int) {
        val currentEvents = prefs.customEvents.toMutableList()
        currentEvents.removeAll { it.id == eventId }
        saveEvents(currentEvents)
    }
    
    fun getEventById(eventId: Int): CustomEvent? {
        return prefs.customEvents.find { it.id == eventId }
    }
    
    fun getAllEvents(): List<CustomEvent> {
        return prefs.customEvents
    }
}