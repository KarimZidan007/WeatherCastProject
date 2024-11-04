//package com.example.weatherproject.navbar.ui.home
//
//import android.location.Location
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.example.weatherproject.model.WeatherApiState
//import com.example.weatherproject.model.repository.remote.FakeRemoteRepository
//import com.example.weatherproject.model.repository.remote.IRemoteRepository
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.test.runBlockingTest
//import org.hamcrest.core.IsEqual
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
//import org.hamcrest.MatcherAssert.assertThat
//
//@RunWith(AndroidJUnit4::class)
//class HomeViewModelTest {
//    lateinit var remoteRepository: IRemoteRepository
//    lateinit var homeViewModel: HomeViewModel
//
//
//    @Before
//    fun setUp() {
//        remoteRepository = FakeRemoteRepository()
//        homeViewModel = HomeViewModel(remoteRepository)
//    }
//
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun getCurrentWeather_Success() = runBlockingTest {
//        // Given
//        val location = Location("").apply { latitude = 23.5; longitude = 55.160 }
//
//        // When
//        homeViewModel.getCurrentWeather(location, "en", "metric", "m/s")
//
//        // Then
//        homeViewModel.currentWeather.collect { state ->
//            when (state) {
//                is WeatherApiState.Success -> {
//                    assertThat(state.currentWeather.cityName, IsEqual("Cairo"))
//                    //assertThat(state.currentWeather.temp, IsEqual(15.0)) // Ensure types match
//                }
//
//                else -> throw AssertionError("Expected Success state")
//            }
//        }
//    }
//}
//
