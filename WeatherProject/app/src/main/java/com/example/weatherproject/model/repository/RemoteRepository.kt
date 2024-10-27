package com.example.mvvm_demo.model.repository

import android.location.Location
import android.util.Log
import com.example.mvvm_demo.model.datasources.RemoteDataSrcImplementation
import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.Root
import com.example.weatherproject.model.pojos.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class RemoteRepository(private var remoteDataSrc: RemoteDataSrcImplementation) : WeatherRepository {


    override suspend fun get5day3hourForecast(location :Location,languague:String): Flow<Root> = flow {
        val response = remoteDataSrc.get5day3hourForecastFromRemoteDataSrc(location,languague)
        emit(response)
    }

    override suspend fun getCurrentWeather(location: Location, languague: String): Flow<WeatherResponse> = flow {
        val response = remoteDataSrc.getCurrentWeatherState(location,languague)
        emit(response)
    }
}


