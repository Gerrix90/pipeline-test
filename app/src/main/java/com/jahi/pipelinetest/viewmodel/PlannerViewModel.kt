package com.jahi.pipelinetest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jahi.pipelinetest.domain.*
import com.jahi.pipelinetest.model.CustomEvent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel() {

    val events: StateFlow<List<CustomEvent>> = getEventsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createEvent(name: String, date: String, showTime: Boolean = false, showInWidget: Boolean = false) {
        viewModelScope.launch {
            createEventUseCase(name, date, showTime, showInWidget)
        }
    }

    fun updateEvent(event: CustomEvent) {
        viewModelScope.launch {
            updateEventUseCase(event)
        }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            deleteEventUseCase(eventId)
        }
    }

    fun getEventById(eventId: Int): CustomEvent? {
        // For synchronous access, get from current state
        return events.value.find { it.id == eventId }
    }
}