package com.example.weatherproject.model.repository.local

import LocalDataSrcImplementation
import com.example.weatherproject.model.datasources.local.ILocalDataSource
import com.example.weatherproject.model.pojos.EventAlerts
import com.example.weatherproject.model.pojos.WeatherDb
import kotlinx.coroutines.flow.Flow


class LocalRepository(var localDataSrc: ILocalDataSource) : ILocalRepository {
    override suspend fun getAllAlertsDB(): Flow<List<EventAlerts>>? {
        return localDataSrc.getAllAlerts()
    }

    override suspend fun deleteAlertDB(alert: EventAlerts) {
        return localDataSrc.deleteAlert(alert)
    }

    override suspend fun insertAlertDB(alert: EventAlerts) {
         localDataSrc.insertAlert(alert)
    }


    override suspend fun getAllFavCities(): Flow<List<WeatherDb>>? {
        return localDataSrc.getAllFavForecast() }


    override suspend fun deleteFavCityDetails(lat:Double,long:Double): Int?{
        return localDataSrc.deleteFavForecast(lat,long)

    }
    override suspend fun insertFavCityDetails(city: WeatherDb): Long? {
        return localDataSrc.insertFavForecast(city)
    }

    override suspend fun updateFavCityDetails(city: WeatherDb) {
        return localDataSrc.updateFavForecast(city)
    }

}