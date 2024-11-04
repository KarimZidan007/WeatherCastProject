package com.example.weatherproject.model.pojos

sealed class DataBaseState {
    class Success(val weatherDetails : List<FullWeatherDetails>) : DataBaseState()
    class Failed(val msg:Throwable) : DataBaseState()
    object Loading : DataBaseState()
}