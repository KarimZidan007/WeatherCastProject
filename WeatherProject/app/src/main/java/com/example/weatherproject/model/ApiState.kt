package com.example.weatherproject.model

import com.example.weatherproject.model.pojos.ForecastFinal

sealed class ApiState {
    class Success(val weatherDetails : List<ForecastFinal>) : ApiState()
    class Failed(val msg:Throwable) : ApiState()
    object Loading : ApiState()
}