package com.example.weatherproject.model.repository

import LocalDataSrcImplementation
import com.example.weatherproject.model.pojos.FavCity
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.pojos.WeatherFinal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class LocalRepository(var localDataSrc: LocalDataSrcImplementation) : FavCityRepository{



    override suspend fun getFavCityWeather(lat: Double, long: Double): Flow<WeatherFinal> {
      return  localDataSrc.getFavWeather(lat,long)
    }

    override suspend fun deleteFavCityWeather(city: WeatherFinal):Int {
        return localDataSrc.deleteFavWeather(city)
    }

    override suspend fun insertFavCityWeather(city: WeatherFinal) :Long {
        return localDataSrc.insertFavWeather(city)
    }

    override suspend fun updateFavCityWeather(city: WeatherFinal) {
        localDataSrc.updateFavWeather(city)
    }




    override suspend fun getAllFavCities(): Flow<List<FullWeatherDetails>> {
        return localDataSrc.getAllFavForecast()   }

    override suspend fun getFavCityDetails(lat: Double, long: Double): Flow<FullWeatherDetails> {
     return emptyFlow()
    }

    override suspend fun deleteFavCityDetails(city: FullWeatherDetails): Int {
        return localDataSrc.deleteFavForecast(city)

    }
    override suspend fun insertFavCityDetails(city: FullWeatherDetails): Long {
        return localDataSrc.insertFavForecast(city)
    }

    override suspend fun updateFavCityDetails(city: FullWeatherDetails) {
        return localDataSrc.updateFavForecast(city)
    }

}