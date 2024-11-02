package com.example.weatherproject.model.repository

import com.example.weatherproject.model.pojos.FavCity
import com.example.weatherproject.model.pojos.ForecastFinal
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.pojos.WeatherFinal
import kotlinx.coroutines.flow.Flow

interface FavCityRepository {

    suspend fun getFavCityWeather(lat:Double , long :Double) :Flow<WeatherFinal>
    suspend fun deleteFavCityWeather(city: WeatherFinal):Int
    suspend fun insertFavCityWeather(city: WeatherFinal):Long
    suspend fun updateFavCityWeather(city:WeatherFinal)

    suspend fun getAllFavCities():Flow<List<FullWeatherDetails>>
    suspend fun getFavCityDetails(lat:Double , long :Double) : Flow<FullWeatherDetails>
    suspend fun deleteFavCityDetails(city: FullWeatherDetails):Int
    suspend fun insertFavCityDetails(city: FullWeatherDetails):Long
    suspend fun updateFavCityDetails(city:FullWeatherDetails)

}