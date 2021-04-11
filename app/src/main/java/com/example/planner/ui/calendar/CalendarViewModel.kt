package com.example.planner.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class GenericEvent(
    val entities: List<Event> = emptyList()
)

class CalendarViewModel : ViewModel() {
    private val _events = MutableLiveData<GenericEvent>()
    val events: LiveData<GenericEvent> = _events

    fun addEvent(event: Event) {
        var eventsList: List<Event> = emptyList()
        eventsList = eventsList + event

        val existingEntities = _events.value?.entities.orEmpty()
        _events.value = GenericEvent(existingEntities + eventsList)
    }

    fun getSize(): Long {
        return if (_events.value?.entities.isNullOrEmpty()) 0 else _events.value?.entities.orEmpty()
            .last().id + 1
    }

    fun removeEvent(event: Event) {
        val existingEntities = _events.value?.entities.orEmpty().toMutableList()
        existingEntities.remove(event)
        _events.value = GenericEvent(existingEntities.toList())
    }
}