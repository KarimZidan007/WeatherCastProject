package com.example.mvvm_demo.model.datasources
import ForecastRemoteDataSource
import com.example.weatherproject.model.pojos.Root

class RemoteDataSrcImplementation(private val remoteDataSrc : ForecastRemoteDataSource) {

    suspend fun get5day3hourForecastFromRemoteDataSrc(): Root
    {
        return remoteDataSrc.getForecastOverNetwork()
    }

}