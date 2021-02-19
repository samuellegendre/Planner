package com.example.planner.ui.study

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudyViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Mode études"
    }
    val text: LiveData<String> = _text

}