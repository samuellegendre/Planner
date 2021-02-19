package com.example.planner.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Paramètres"
    }
    val text: LiveData<String> = _text
}