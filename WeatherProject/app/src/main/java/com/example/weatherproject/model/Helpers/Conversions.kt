package com.example.weatherproject.model.Helpers

import android.content.Context
import android.location.Geocoder
import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.ForecastDB
import com.example.weatherproject.model.pojos.ForecastFinal
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.pojos.Root
import com.example.weatherproject.model.pojos.WeatherDb
import com.example.weatherproject.model.pojos.WeatherFinal
import java.util.Locale
import kotlin.math.roundToInt

object  Conversions {

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

    fun convertCurrentWeatherObj(
        tempObj: WeatherResponse,
        tempUnit: String,
        windUnit: String,
        languague: String
    ): WeatherFinal {
        var temperatureUnitSymbol = "°"
        var windUnitSymbol = "m/s"
        var result = WeatherFinal()
        result.desc = tempObj.weather[0].description
        result.icon = tempObj.weather[0].icon
        result.latitude=tempObj.coord.lat
        result.longitude=tempObj.coord.lon

        if (languague == "ar") {
            result.cityName = tempObj.name
            result.pressure = convertToArabicNumerals(tempObj.main.pressure.toString()) + " هكتوبسكال"
            result.humidity = convertToArabicNumerals(tempObj.main.humidity.toString()) + " %"

            // Temperature conversion and rounding
            when (tempUnit) {
                "celsius" -> {
                    result.temp = convertToArabicNumerals(tempObj.main.temp.roundToInt().toString()) + " س°"
                    result.maxTemp = convertToArabicNumerals(tempObj.main.temp_max.roundToInt().toString()) + " س°"
                    result.minTemp = convertToArabicNumerals(tempObj.main.temp_min.roundToInt().toString()) + " س°"
                }
                "kelvin" -> {
                    result.temp = convertToArabicNumerals(celsiusToKelvin(tempObj.main.temp).roundToInt().toString()) + " ك°"
                    result.maxTemp = convertToArabicNumerals(celsiusToKelvin(tempObj.main.temp_max).roundToInt().toString()) + " ك°"
                    result.minTemp = convertToArabicNumerals(celsiusToKelvin(tempObj.main.temp_min).roundToInt().toString()) + " ك°"
                }
                "fahrenheit" -> {
                    result.temp = convertToArabicNumerals(celsiusToFahrenheit(tempObj.main.temp).roundToInt().toString()) + " ف°"
                    result.maxTemp = convertToArabicNumerals(celsiusToFahrenheit(tempObj.main.temp_max).roundToInt().toString()) + " ف°"
                    result.minTemp = convertToArabicNumerals(celsiusToFahrenheit(tempObj.main.temp_min).roundToInt().toString()) + " ف°"
                }
            }

            // Wind speed conversion and rounding
            result.windSpeed = when (windUnit) {
                "mile_hour" -> {
                    windUnitSymbol = "ميل/س"
                    convertToArabicNumerals(metersPerSecondToMilesPerHour(tempObj.wind.speed).roundToInt().toString()) + " " + windUnitSymbol
                }
                "meter_sec" -> {
                    windUnitSymbol = "م/ث"
                    convertToArabicNumerals(tempObj.wind.speed.roundToInt().toString()) + " " + windUnitSymbol
                }
                else -> tempObj.wind.speed.toString()  // Default case
            }

        } else {
            result.cityName = tempObj.name
            result.pressure = tempObj.main.pressure.toString() + " hpa"
            result.humidity = tempObj.main.humidity.toString() + " %"

            // Temperature conversion and rounding
            when (tempUnit) {
                "celsius" -> {
                    result.temp = tempObj.main.temp.roundToInt().toString() + " c°"
                    result.maxTemp = tempObj.main.temp_max.roundToInt().toString() + " c°"
                    result.minTemp = tempObj.main.temp_min.roundToInt().toString() + " c°"
                }
                "kelvin" -> {
                    result.temp = celsiusToKelvin(tempObj.main.temp).roundToInt().toString() + " k°"
                    result.maxTemp = celsiusToKelvin(tempObj.main.temp_max).roundToInt().toString() + " k°"
                    result.minTemp = celsiusToKelvin(tempObj.main.temp_min).roundToInt().toString() + " k°"
                }
                "fahrenheit" -> {
                    result.temp = celsiusToFahrenheit(tempObj.main.temp).roundToInt().toString() + " f°"
                    result.maxTemp = celsiusToFahrenheit(tempObj.main.temp_max).roundToInt().toString() + " f°"
                    result.minTemp = celsiusToFahrenheit(tempObj.main.temp_min).roundToInt().toString() + " f°"
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


    fun convertCurrentWeatherDB(
        tempObj: WeatherDb,
        tempUnit: String,
        windUnit: String,
        languague: String,
        geocoder: Geocoder

    ): FullWeatherDetails {


        var temperatureUnitSymbol = "°"
        var windUnitSymbol = "m/s"
        var result = FullWeatherDetails()
        result.icon = tempObj.icon
        result.latitude=tempObj.lat_
        result.longitude=tempObj.lng_

        if (languague == "ar") {
            result.cityName = tempObj.cityNameArabic

            result.pressure = convertToArabicNumerals(tempObj.pressure.toString()) + " هكتوبسكال"
            result.humidity = convertToArabicNumerals(tempObj.humidity.toString()) + " %"
            result.desc = tempObj.descArabic

            // Temperature conversion and rounding
            when (tempUnit) {
                "celsius" -> {
                    result.temp = convertToArabicNumerals(tempObj.temp.roundToInt().toString()) + " س°"
                    result.maxTemp = convertToArabicNumerals(tempObj.maxTemp.roundToInt().toString()) + " س°"
                    result.minTemp = convertToArabicNumerals(tempObj.minTemp.roundToInt().toString()) + " س°"
                }
                "kelvin" -> {
                    result.temp = convertToArabicNumerals(celsiusToKelvin(tempObj.temp).roundToInt().toString()) + " ك°"
                    result.maxTemp = convertToArabicNumerals(celsiusToKelvin(tempObj.maxTemp).roundToInt().toString()) + " ك°"
                    result.minTemp = convertToArabicNumerals(celsiusToKelvin(tempObj.minTemp).roundToInt().toString()) + " ك°"
                }
                "fahrenheit" -> {
                    result.temp = convertToArabicNumerals(celsiusToFahrenheit(tempObj.temp).roundToInt().toString()) + " ف°"
                    result.maxTemp = convertToArabicNumerals(celsiusToFahrenheit(tempObj.maxTemp).roundToInt().toString()) + " ف°"
                    result.minTemp = convertToArabicNumerals(celsiusToFahrenheit(tempObj.minTemp).roundToInt().toString()) + " ف°"
                }
            }

            // Wind speed conversion and rounding
            result.windSpeed = when (windUnit) {
                "mile_hour" -> {
                    windUnitSymbol = "ميل/س"
                    convertToArabicNumerals(metersPerSecondToMilesPerHour(tempObj.windSpeed).roundToInt().toString()) + " " + windUnitSymbol
                }
                "meter_sec" -> {
                    windUnitSymbol = "م/ث"
                    convertToArabicNumerals(tempObj.windSpeed.roundToInt().toString()) + " " + windUnitSymbol
                }
                else -> tempObj.windSpeed.toString()  // Default case
            }

        } else  {
            result.desc = tempObj.descEnglish
            // English language setup
            result.pressure = tempObj.pressure.toString() + " hpa"
            result.humidity = tempObj.humidity.toString() + " %"

            when (tempUnit) {
                "celsius" -> {
                    result.temp = tempObj.temp.roundToInt().toString() + " c°"
                    result.maxTemp = tempObj.maxTemp.roundToInt().toString() + " c°"
                    result.minTemp = tempObj.minTemp.roundToInt().toString() + " c°"
                }
                "kelvin" -> {
                    result.temp = celsiusToKelvin(tempObj.temp).roundToInt().toString() + " k°"
                    result.maxTemp = celsiusToKelvin(tempObj.maxTemp).roundToInt().toString() + " k°"
                    result.minTemp = celsiusToKelvin(tempObj.minTemp).roundToInt().toString() + " k°"
                }
                "fahrenheit" -> {
                    result.temp = celsiusToFahrenheit(tempObj.temp).roundToInt().toString() + " f°"
                    result.maxTemp = celsiusToFahrenheit(tempObj.maxTemp).roundToInt().toString() + " f°"
                    result.minTemp = celsiusToFahrenheit(tempObj.minTemp).roundToInt().toString() + " f°"
                }
            }

            // Wind speed conversion and rounding
            result.windSpeed = when (windUnit) {
                "mile_hour" -> {
                    windUnitSymbol = " mile/h"
                    metersPerSecondToMilesPerHour(tempObj.windSpeed).roundToInt().toString() + windUnitSymbol
                }
                "meter_sec" -> {
                    tempObj.windSpeed.roundToInt().toString() + " m/s"
                }
                else -> tempObj.windSpeed.toString()
            }
        }


        return result
    }




    private fun fahrenheitToCelsius(fahrenheit: Double): Double {
        return (fahrenheit - 32) * 5 / 9
    }

    private fun kelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    fun convertWeatherDbListToFullWeatherDetailsList(
        weatherDbList: List<WeatherDb>,
        tempUnit: String,
        windUnit:String,
        language: String,
        geocoder: Geocoder

    ): List<FullWeatherDetails>{
        val fullWeatherDetailsList = mutableListOf<FullWeatherDetails>()
        for (weatherDb in weatherDbList) {

            val weatherForecast = convertWeatherDB(weatherDb.weatherForecast,tempUnit,language)
            val fullWeather = convertCurrentWeatherDB(weatherDb, tempUnit, windUnit, language,geocoder)
            fullWeather.weatherForecast = weatherForecast

            if(language=="ar")
            {
                fullWeather.cityName=weatherDb.cityNameArabic
                fullWeather.country=weatherDb.countryArabic
                fullWeather.address=weatherDb.addressArabic

            }
            else if(language=="en")
            {
                fullWeather.cityName=weatherDb.cityNameEnglish
                fullWeather.country=weatherDb.countryEnglish
                fullWeather.address=weatherDb.addressEnglish
            }
            else
            {
                fullWeather.cityName=weatherDb.cityNameRomanian
                fullWeather.country=weatherDb.countryRomanion
                fullWeather.cityName=weatherDb.addressRomanion
            }
            fullWeatherDetailsList.add(fullWeather)
        }
        return fullWeatherDetailsList
    }

    fun convert5Days3HoursObj(tempObj: Root, tempUnit: String, language: String): List<ForecastFinal> {
        val result: Root = tempObj
        val finalList = mutableListOf<ForecastFinal>()

        if (language == "ar") {
            when (tempUnit) {
                "celsius" -> {
                    for (i in 0 until result.list.size) {
                        val forecast = ForecastFinal().apply {
                            temp = Conversions.convertToArabicNumerals(result.list[i].main.temp.roundToInt().toString()) + "س°"
                            icon = result.list[i].weather[0].icon
                            dt_txt = result.list[i].dt_txt

                        }
                        finalList.add(forecast)
                    }
                }
                "kelvin" -> {
                    for (i in 0 until result.list.size) {
                        val forecast = ForecastFinal().apply {
                            temp = Conversions.convertToArabicNumerals(celsiusToKelvin(result.list[i].main.temp).roundToInt().toString()) + "ك°"
                            icon = result.list[i].weather[0].icon
                            dt_txt = result.list[i].dt_txt

                        }
                        finalList.add(forecast)
                    }
                }
                "fahrenheit" -> {
                    for (i in 0 until result.list.size) {
                        val forecast = ForecastFinal().apply {
                            temp = Conversions.convertToArabicNumerals(celsiusToFahrenheit(result.list[i].main.temp).roundToInt().toString()) + "ف°"
                            icon = result.list[i].weather[0].icon
                            dt_txt = result.list[i].dt_txt

                        }
                        finalList.add(forecast)
                    }
                }
            }
        } else {
            when (tempUnit) {
                "celsius" -> {
                    for (i in 0 until result.list.size) {
                        val forecast = ForecastFinal().apply {
                            temp = result.list[i].main.temp.roundToInt().toString() + " °C"
                            icon = result.list[i].weather[0].icon
                            dt_txt = result.list[i].dt_txt

                        }
                        finalList.add(forecast)
                    }
                }
                "kelvin" -> {
                    for (i in 0 until result.list.size) {
                        val forecast = ForecastFinal().apply {
                            temp = celsiusToKelvin(result.list[i].main.temp).roundToInt().toString() + " °K"
                            icon = result.list[i].weather[0].icon
                            dt_txt = result.list[i].dt_txt

                        }
                        finalList.add(forecast)
                    }
                }
                "fahrenheit" -> {
                    for (i in 0 until result.list.size) {
                        val forecast = ForecastFinal().apply {
                            temp = celsiusToFahrenheit(result.list[i].main.temp).roundToInt().toString() + " °F"
                            icon = result.list[i].weather[0].icon
                            dt_txt = result.list[i].dt_txt

                        }
                        finalList.add(forecast)
                    }
                }
            }
        }

        return finalList
    }

    private fun convertWeatherDB(tempObj: List<ForecastDB>, tempUnit: String, language: String): List<ForecastFinal> {
        val result: List<ForecastDB> = tempObj
        val finalList = mutableListOf<ForecastFinal>()

        for (i in result.indices) {

            val forecast = ForecastFinal().apply {
                temp = when {
                    language == "ar" && tempUnit == "celsius" -> {
                        Conversions.convertToArabicNumerals(result[i].temp.roundToInt().toString()) + "س°"
                    }
                    language == "ar" && tempUnit == "kelvin" -> {
                        Conversions.convertToArabicNumerals(celsiusToKelvin(result[i].temp).roundToInt().toString()) + "ك°"
                    }
                    language == "ar" && tempUnit == "fahrenheit" -> {
                        Conversions.convertToArabicNumerals(celsiusToFahrenheit(result[i].temp).roundToInt().toString()) + "ف°"
                    }
                    tempUnit == "celsius" -> {
                        result[i].temp.roundToInt().toString() + " °C"
                    }
                    tempUnit == "kelvin" -> {
                        celsiusToKelvin(result[i].temp).roundToInt().toString() + " °K"
                    }
                    tempUnit == "fahrenheit" -> {
                        celsiusToFahrenheit(result[i].temp).roundToInt().toString() + " °F"
                    }
                    else -> "Unknown"
                }
                icon = result[i].icon
                dt_txt = result[i].dt_txt
            }
            finalList.add(forecast)
        }
        return finalList
    }

    private fun translateCityName(latitude: Double, longitude: Double, lang: String, geocoder: Geocoder): String {
        return try {
            val locale = Locale(lang)

            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.locality ?: "Unknown City"
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown City"
        }
    }


}