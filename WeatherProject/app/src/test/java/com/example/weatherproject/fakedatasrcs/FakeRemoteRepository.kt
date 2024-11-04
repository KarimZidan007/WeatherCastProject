package com.example.weatherproject.model.repository.remote

import android.location.Location
import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRemoteRepository : IRemoteRepository {
    override suspend fun get5day3hourForecast(location: Location, languague: String): Flow<Root> {
        TODO("Not yet implemented")
    }


    override suspend fun getCurrentWeather(location: Location, language: String): Flow<WeatherResponse> = flow {
       var response= WeatherResponse()
        //response.name = "Cairo"
        response.main.temp= 15.0
        emit(
            response
        )
    }
}
