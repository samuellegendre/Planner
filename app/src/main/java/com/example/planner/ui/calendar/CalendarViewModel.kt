package com.example.planner.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {
    private val _events = MutableLiveData<List<MyEvent>>()
    val events: LiveData<List<MyEvent>> = _events
}