package com.example.weatherproject.model.datasources.local

import com.example.weatherproject.database.event.EventDao
import com.example.weatherproject.database.forecast.ForecastDAO
import com.example.weatherproject.model.pojos.EventAlerts
import com.example.weatherproject.model.pojos.WeatherDb
import kotlinx.coroutines.flow.Flow



interface ILocalDataSource {
    suspend fun getAllAlerts(): Flow<List<EventAlerts>>?
    suspend fun insertAlert(alert: EventAlerts)
    suspend fun deleteAlert(alert: EventAlerts)

    suspend fun getAllFavForecast(): Flow<List<WeatherDb>>?
    suspend fun insertFavForecast(city: WeatherDb): Long?
    suspend fun deleteFavForecast(lat: Double, long: Double): Int?
    suspend fun updateFavForecast(city: WeatherDb)
}
