package com.example.weatherproject.model.pojos


sealed class EventState {
    class Success(val events : List<EventAlerts>) : EventState()
    class Failed(val msg:Throwable) : EventState()
    object Loading : EventState()
}
