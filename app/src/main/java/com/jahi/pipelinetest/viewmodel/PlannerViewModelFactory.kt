package com.jahi.pipelinetest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jahi.pipelinetest.domain.*

class PlannerViewModelFactory(
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getEventsUseCase: GetEventsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlannerViewModel::class.java)) {
            return PlannerViewModel(
                createEventUseCase,
                updateEventUseCase,
                deleteEventUseCase,
                getEventsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}