package com.example.mvvm_demo.model.datasources
import ForecastRemoteDataSource
import android.location.Location
import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.Root

class RemoteDataSrcImplementation(private val remoteDataSrc : ForecastRemoteDataSource) {

    suspend fun get5day3hourForecastFromRemoteDataSrc(location: Location,languague:String): Root
    {
        return remoteDataSrc.getForecastOverNetwork(location,languague)
    }

    suspend fun getCurrentWeatherState(location: Location,languague:String): WeatherResponse
    {
        return remoteDataSrc.getCurrentWeatherOverNetwork(location,languague)
    }

}