package com.example.mvvm_demo.model.repository

import com.example.weatherproject.model.pojos.Root
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
     suspend fun get5day3hourForecast():Flow<Root>
}