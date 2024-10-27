package com.example.labone.retrofit

import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.Root
import com.example.weatherproject.model.pojos.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface Services {

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units:String,
        @Query("appid") apiKey: String,
        @Query("lang") languague: String
    ): Root

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units:String,
        @Query("appid") apiKey: String,
        @Query("lang") languague: String
    ): WeatherResponse
}