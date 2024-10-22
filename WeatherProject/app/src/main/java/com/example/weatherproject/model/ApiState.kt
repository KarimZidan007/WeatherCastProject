package com.example.weatherproject.model

import com.example.weatherproject.model.pojos.Root

sealed class ApiState {
    class Success(val weatherDetails : Root) : ApiState()
    class Failed(val msg:Throwable) : ApiState()
    object Loading : ApiState()
}