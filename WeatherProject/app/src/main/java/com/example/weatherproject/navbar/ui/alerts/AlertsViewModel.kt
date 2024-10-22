package com.example.weatherproject.navbar.ui.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlertsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is ِAlerts Fragment"
    }
    val text: LiveData<String> = _text
}