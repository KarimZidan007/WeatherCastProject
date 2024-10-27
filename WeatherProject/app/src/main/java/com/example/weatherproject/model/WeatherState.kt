package com.example.weatherproject.model

import com.example.weatherproject.model.pojos.WeatherFinal


sealed class WeatherApiState {
    class Success(val currentWeather : WeatherFinal) : WeatherApiState()
    class Failed(val msg:Throwable) : WeatherApiState()
    object Loading : WeatherApiState()
}