package com.example.weatherproject.navbar.ui.home


import android.location.Location
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mvvm_demo.model.repository.RemoteRepository
import com.example.weatherproject.model.ApiState
import com.example.weatherproject.model.WeatherApiState
import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.Root
import com.example.weatherproject.model.pojos.Weather
import com.example.weatherproject.model.pojos.WeatherFinal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class HomeViewModel(private var remoteRepository:RemoteRepository) : ViewModel() {

    private val _weatherDetailsStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherDetailsStateFlow: StateFlow<ApiState> = _weatherDetailsStateFlow

    private val _currentWeather = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    var currentWeather: StateFlow<WeatherApiState> = _currentWeather



    var temperatureUnitSymbol = "°"
    var windUnitSymbol = "m/s"
    fun getCurrentWeather(
        location: Location,
        languague: String,
        tempUnit: String,
        windUnit: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        remoteRepository.getCurrentWeather(location, languague)
            .catch {
                _currentWeather.value = WeatherApiState.Failed(it)
            }
            .collect {
                var result = convertCurrentWeatherObj(it, tempUnit, windUnit, languague)
                _currentWeather.value = WeatherApiState.Success(result)
            }
    }

    fun getWeatherDetails5days3hours(location: Location, languague: String, tempUnit: String) =
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepository.get5day3hourForecast(location, languague)
                .catch {
                    _weatherDetailsStateFlow.value = ApiState.Failed(it)
                }
                .collect {
                    var result = convert5Days3HoursObj(it, tempUnit, languague)
                    _weatherDetailsStateFlow.value = ApiState.Success(result)
                }
        }

    fun convert5Days3HoursObj(tempObj: Root, tempUnit: String, languague: String): Root {
        var result: Root = tempObj
        if (languague == "ar") {
            when (tempUnit) {
                "celsius" -> {
                }

                "kelvin" -> {

                    for (i in 0..result.list.size - 1) {
                        result.list.get(i).main.temp = celsiusToKelvin(result.list.get(i).main.temp)
                    }
                }

                "fahrenheit" -> {
                    for (i in 0..result.list.size - 1) {
                        result.list.get(i).main.temp =
                            celsiusToFahrenheit(result.list.get(i).main.temp)
                    }
                }
            }
        } else {
            when (tempUnit) {
                "celsius" -> {
                }

                "kelvin" -> {

                    for (i in 0..result.list.size - 1) {
                        result.list.get(i).main.temp = celsiusToKelvin(result.list.get(i).main.temp)
                    }
                }

                "fahrenheit" -> {
                    temperatureUnitSymbol = "F°"
                    for (i in 0..result.list.size - 1) {
                        result.list.get(i).main.temp =
                            celsiusToFahrenheit(result.list.get(i).main.temp)
                    }
                }
            }
        }

        return result
    }

    fun convertCurrentWeatherObj(
        tempObj: WeatherResponse,
        tempUnit: String,
        windUnit: String,
        languague: String
    ): WeatherFinal {
        var result = WeatherFinal()
        result.cityName = tempObj.name
        result.desc = tempObj.weather[0].description
        result.icon = tempObj.weather[0].icon

        if (languague == "ar") {
            result.pressure = convertToArabicNumerals(tempObj.main.pressure.toString()) + " هكتوبسكال"
            result.humidity = convertToArabicNumerals(tempObj.main.humidity.toString()) + " %"

            // Temperature conversion and rounding
            when (tempUnit) {
                "celsius" -> {
                    result.temp = convertToArabicNumerals(tempObj.main.temp.roundToInt().toString()) + " °"
                    result.maxTemp = convertToArabicNumerals(tempObj.main.temp_max.roundToInt().toString()) + " °"
                    result.minTemp = convertToArabicNumerals(tempObj.main.temp_min.roundToInt().toString()) + " °"
                }
                "kelvin" -> {
                    result.temp = convertToArabicNumerals(celsiusToKelvin(tempObj.main.temp).roundToInt().toString()) + " °"
                    result.maxTemp = convertToArabicNumerals(celsiusToKelvin(tempObj.main.temp_max).roundToInt().toString()) + " °"
                    result.minTemp = convertToArabicNumerals(celsiusToKelvin(tempObj.main.temp_min).roundToInt().toString()) + " °"
                }
                "fahrenheit" -> {
                    result.temp = convertToArabicNumerals(celsiusToFahrenheit(tempObj.main.temp).roundToInt().toString()) + " °"
                    result.maxTemp = convertToArabicNumerals(celsiusToFahrenheit(tempObj.main.temp_max).roundToInt().toString()) + " °"
                    result.minTemp = convertToArabicNumerals(celsiusToFahrenheit(tempObj.main.temp_min).roundToInt().toString()) + " °"
                }
            }

            // Wind speed conversion and rounding
            result.windSpeed = when (windUnit) {
                "mile_hour" -> {
                    windUnitSymbol = "ميل/ساعة"
                    convertToArabicNumerals(metersPerSecondToMilesPerHour(tempObj.wind.speed).roundToInt().toString()) + " " + windUnitSymbol
                }
                "meter_sec" -> {
                    windUnitSymbol = "متر/ثانية"
                    convertToArabicNumerals(tempObj.wind.speed.roundToInt().toString()) + " " + windUnitSymbol
                }
                else -> tempObj.wind.speed.toString()  // Default case
            }

        } else {
            // English language setup
            result.pressure = tempObj.main.pressure.toString() + " hpa"
            result.humidity = tempObj.main.humidity.toString() + " %"

            // Temperature conversion and rounding
            when (tempUnit) {
                "celsius" -> {
                    result.temp = tempObj.main.temp.roundToInt().toString() + " °"
                    result.maxTemp = tempObj.main.temp_max.roundToInt().toString() + " °"
                    result.minTemp = tempObj.main.temp_min.roundToInt().toString() + " °"
                }
                "kelvin" -> {
                    result.temp = celsiusToKelvin(tempObj.main.temp).roundToInt().toString() + " °"
                    result.maxTemp = celsiusToKelvin(tempObj.main.temp_max).roundToInt().toString() + " °"
                    result.minTemp = celsiusToKelvin(tempObj.main.temp_min).roundToInt().toString() + " °"
                }
                "fahrenheit" -> {
                    result.temp = celsiusToFahrenheit(tempObj.main.temp).roundToInt().toString() + " °"
                    result.maxTemp = celsiusToFahrenheit(tempObj.main.temp_max).roundToInt().toString() + " °"
                    result.minTemp = celsiusToFahrenheit(tempObj.main.temp_min).roundToInt().toString() + " °"
                }
            }

            // Wind speed conversion and rounding
            result.windSpeed = when (windUnit) {
                "mile_hour" -> {
                    windUnitSymbol = " mile/h"
                    metersPerSecondToMilesPerHour(tempObj.wind.speed).roundToInt().toString() + windUnitSymbol
                }
                "meter_sec" -> {
                    tempObj.wind.speed.roundToInt().toString() + " m/s"
                }
                else -> tempObj.wind.speed.toString()  // Default case
            }
        }

        return result
    }

    private fun celsiusToKelvin(celsius: Double): Double {
        return celsius + 273.15 .roundToInt()
    }
    private fun celsiusToFahrenheit(celsius: Double): Double {
        return ((celsius * 9 / 5) + 32)
    }
    private fun metersPerSecondToMilesPerHour(metersPerSecond: Double): Double {
        return metersPerSecond * 2.23694 .roundToInt()
    }
    fun convertToArabicNumerals(englishNumber: String): String {
        val englishToArabicMap = mapOf(
            '0' to '٠',
            '1' to '١',
            '2' to '٢',
            '3' to '٣',
            '4' to '٤',
            '5' to '٥',
            '6' to '٦',
            '7' to '٧',
            '8' to '٨',
            '9' to '٩'
        )

        return englishNumber.map { char ->
            englishToArabicMap[char] ?: char
        }.joinToString("")
    }

}
class MyWeather3hours5daysFactory(private var remoteRepository:RemoteRepository ): ViewModelProvider.Factory{
    public override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(remoteRepository) as T
    }


}
