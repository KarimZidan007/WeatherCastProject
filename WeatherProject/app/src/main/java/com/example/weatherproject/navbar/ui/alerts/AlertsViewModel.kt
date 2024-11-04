package com.example.weatherproject.navbar.ui.alerts


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherproject.model.pojos.EventAlerts

import com.example.weatherproject.model.pojos.EventState
import com.example.weatherproject.model.repository.local.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertsViewModel(private var localRepository: LocalRepository) : ViewModel() {
init {
    getAllEvents()
}
    private var alertsList_ = MutableStateFlow<EventState>(EventState.Loading)
    var alertsList: StateFlow<EventState> = alertsList_

    private var _alertSetter= MutableLiveData<EventAlerts>()
    var alertSetter:LiveData<EventAlerts> =_alertSetter

    fun updateLastEvent(event: EventAlerts) {
        _alertSetter.value=event
        Log.i("value", event.date)
    }

    fun getAllEvents() =
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.getAllAlertsDB()
                ?.catch {
                    alertsList_.value = EventState.Failed(it)
                }
                ?.collect {
                    alertsList_.value = EventState.Success(it)
                }
        }

    fun insertEvent(alert: EventAlerts) = viewModelScope.launch(Dispatchers.IO) {
        localRepository.insertAlertDB(alert)
        getAllEvents()
    }

    fun deleteEvent(alert:EventAlerts)= viewModelScope.launch(Dispatchers.IO) {
        localRepository.deleteAlertDB(alert)
        getAllEvents()
    }
}

class AlertsFactory(private var localRepository: LocalRepository): ViewModelProvider.Factory {
    public override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertsViewModel(localRepository) as T
    }
}

