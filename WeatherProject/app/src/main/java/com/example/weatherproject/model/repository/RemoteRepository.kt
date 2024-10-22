package com.example.mvvm_demo.model.repository

import com.example.mvvm_demo.model.datasources.RemoteDataSrcImplementation
import com.example.weatherproject.model.pojos.Root
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class RemoteRepository(private var remoteDataSrc: RemoteDataSrcImplementation) : WeatherRepository {
    override suspend fun get5day3hourForecast(): Flow<Root> = flow {
        val response = remoteDataSrc.get5day3hourForecastFromRemoteDataSrc()
        emit(response)
    }
}


