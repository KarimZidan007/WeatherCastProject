package com.example.weatherproject.model.repository.local

import com.example.weatherproject.model.pojos.EventAlerts
import com.example.weatherproject.model.pojos.WeatherDb
import kotlinx.coroutines.flow.Flow

interface ILocalRepository {

    suspend fun getAllAlertsDB() : Flow<List<EventAlerts>>?
    suspend fun deleteAlertDB(alert: EventAlerts)
    suspend fun insertAlertDB(alert: EventAlerts)

    suspend fun getAllFavCities():Flow<List<WeatherDb>>?
    suspend fun deleteFavCityDetails(lat:Double,long:Double):Int?
    suspend fun insertFavCityDetails(city: WeatherDb):Long?
    suspend fun updateFavCityDetails(city:WeatherDb)

}