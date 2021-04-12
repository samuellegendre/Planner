package com.example.planner.ui.calendar

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class GenericEvent(
    val entities: List<Event> = emptyList()
)

class CalendarViewModel : ViewModel() {
    private val _events = MutableLiveData<GenericEvent>()
    val events: LiveData<GenericEvent> = _events
    private val format = Json { prettyPrint = true }
    private val fileName = "data"

    fun addEvent(event: Event) {
        var eventsList: List<Event> = emptyList()
        eventsList = eventsList + event

        val existingEntities = _events.value?.entities.orEmpty()
        _events.value = GenericEvent(existingEntities + eventsList)
    }

    fun getLastId(): Long {
        return if (_events.value?.entities.isNullOrEmpty()) 0 else _events.value?.entities.orEmpty()
            .last().id + 1
    }

    fun removeEvent(event: Event) {
        val existingEntities = _events.value?.entities.orEmpty().toMutableList()
        existingEntities.remove(event)
        _events.value = GenericEvent(existingEntities.toList())
    }

    fun saveEvents(context: Context) {
        val fileContents = format.encodeToString(_events.value?.entities.orEmpty())
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun fetchEvents(context: Context) {
        val fileContents = context.openFileInput(fileName).bufferedReader().readText()
        val data = format.decodeFromString<List<Event>>(fileContents)
        _events.value = GenericEvent(data)
    }
}