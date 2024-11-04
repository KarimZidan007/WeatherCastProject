//package com.example.weatherproject.model.repository
//
//import android.location.Location
//import com.example.mvvm_demo.model.datasources.RemoteDataSrcImplementation
//import com.example.weatherproject.model.repository.remote.RemoteRepository
//import com.example.weatherproject.model.WeatherResponse
//import kotlinx.coroutines.flow.toList
//import kotlinx.coroutines.runBlocking
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.mockito.Mockito.mock
//import org.mockito.Mockito.`when`
//import kotlin.test.Test
//
//class RemoteRepositoryTest
//{
//    private lateinit var remoteDataSrc: RemoteDataSrcImplementation
//    private lateinit var repository: RemoteRepository
//    @Before
//    fun setUp() {
//        remoteDataSrc = mock(RemoteDataSrcImplementation::class.java)
//        repository = RemoteRepository(remoteDataSrc)
//    }
//
//
//    @Test
//    fun `test get5day3hourForecast returns valid data`() = runBlocking {
//        // Arrange
//        val location = Location("provider").apply {
//            latitude = 0.0
//            longitude = 0.0
//        }
//        val expectedResponse = // mock your expected Root response
//            `when`(remoteDataSrc.get5day3hourForecastFromRemoteDataSrc(location, "en")).thenReturn(expectedResponse)
//
//        // Act
//        val result = repository.get5day3hourForecast(location, "en").toList() // Collect Flow
//
//        // Assert
//        assertEquals(expectedResponse, result[0])
//    }
//
//    @Test
//    fun `test getCurrentWeather returns valid data`() = runBlocking {
//        // Arrange
//        val expectedWeatherResponse = WeatherResponse()
//
//        val location = Location("provider").apply {
//            latitude = 0.0
//            longitude = 0.0
//        }
//        val expectedResponse = // mock your expected WeatherResponse
//            `when`(remoteDataSrc.getCurrentWeatherState(location, "en")).thenReturn(expectedResponse)
//
//        // Act
//        val result = repository.getCurrentWeather(location, "en").toList() // Collect Flow
//
//        // Assert
//        assertEquals(expectedResponse, result[0])
//    }
//}
//
//}