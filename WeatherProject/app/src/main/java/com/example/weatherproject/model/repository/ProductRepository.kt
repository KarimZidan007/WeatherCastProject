package com.example.mvvm_demo.model.repository

import android.location.Location
import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.Root
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
     suspend fun get5day3hourForecast(location : Location,languague:String):Flow<Root>
     suspend fun getCurrentWeather(location : Location,languague:String):Flow<WeatherResponse>
}