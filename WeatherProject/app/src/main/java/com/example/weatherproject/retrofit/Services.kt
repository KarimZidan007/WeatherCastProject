package com.example.labone.retrofit

import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.Root
import retrofit2.http.GET
import retrofit2.http.Query

interface Services {

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units:String,
        @Query("appid") apiKey: String
    ): Root

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units:String,
        @Query("appid") apiKey: String
    ): WeatherResponse
}