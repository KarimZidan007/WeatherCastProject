package com.example.weatherproject.fakedatasrcs

import com.example.weatherproject.model.datasources.local.ILocalDataSource
import com.example.weatherproject.model.pojos.EventAlerts
import com.example.weatherproject.model.pojos.WeatherDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSrc(
    val alertsList: MutableList<EventAlerts> = mutableListOf(),
    val forecastsList: MutableList<WeatherDb> = mutableListOf()
) : ILocalDataSource {

    override suspend fun getAllAlerts(): Flow<List<EventAlerts>> = flow {
        emit(alertsList)
    }

    override suspend fun insertAlert(alert: EventAlerts) {
        alertsList.add(alert)
    }

    override suspend fun deleteAlert(alert: EventAlerts) {
        alertsList.remove(alert)
    }

    override suspend fun getAllFavForecast(): Flow<List<WeatherDb>> = flow {
        emit(forecastsList)
    }

    override suspend fun insertFavForecast(city: WeatherDb): Long {
        forecastsList.add(city)
        return city.id // Assuming WeatherDb has an id property
    }

    override suspend fun deleteFavForecast(lat: Double, long: Double): Int {
        val itemToRemove = forecastsList.find { it.lat_ == lat && it.lng_ == long }
        return if (itemToRemove != null) {
            forecastsList.remove(itemToRemove)
            1
        } else {
            0
        }
    }

    override suspend fun updateFavForecast(city: WeatherDb) {
        val index = forecastsList.indexOfFirst { it.id == city.id }
        if (index != -1) {
            forecastsList[index] = city
        }
    }
}
