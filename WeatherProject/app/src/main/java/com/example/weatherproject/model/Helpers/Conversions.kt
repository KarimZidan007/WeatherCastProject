package com.example.weatherproject.model.Helpers

import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.ForecastFinal
import com.example.weatherproject.model.pojos.Root
import com.example.weatherproject.model.pojos.WeatherFinal
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
        result.cityName = tempObj.name
        result.desc = tempObj.weather[0].description
        result.icon = tempObj.weather[0].icon
        result.latitude=tempObj.coord.lat
        result.longitude=tempObj.coord.lon

        if (languague == "ar") {
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
            // English language setup
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

    fun convertForecastList(
        forecastList: List<ForecastFinal>,
        tempUnit: String,
        language: String
    ): List<ForecastFinal> {
        return forecastList.map { forecast ->
            val tempString = forecast.temp
            var finalTempValue = 0.0
            var originalUnit: String? = null

            // Check if the temperature string is in Arabic or English
            if (language == "ar") {
                // Arabic language logic
                when {
                    tempString.contains("س") -> {
                        // Celsius in Arabic
                        originalUnit = "س"
                        finalTempValue = tempString.substringBefore(" س").toDoubleOrNull() ?: 0.0
                    }
                    tempString.contains("ك") -> {
                        // Kelvin in Arabic
                        originalUnit = "ك"
                        finalTempValue = tempString.substringBefore(" ك").toDoubleOrNull() ?: 0.0
                    }
                    tempString.contains("ف") -> {
                        // Fahrenheit in Arabic
                        originalUnit = "ف"
                        finalTempValue = tempString.substringBefore(" ف").toDoubleOrNull() ?: 0.0
                    }
                }

                // Convert based on the requested unit
                val newTemp = when (tempUnit) {
                    "celsius" -> {
                        // No conversion needed
                        "$finalTempValue س°"
                    }
                    "kelvin" -> {
                        if (originalUnit == "س") {
                            // Convert from Celsius to Kelvin
                            val kelvinValue = celsiusToKelvin(finalTempValue).roundToInt()
                            "$kelvinValue ك°"
                        } else if (originalUnit == "ك") {
                            // No conversion needed, just output in Arabic Kelvin
                            "$finalTempValue ك°"
                        } else {
                            // Convert from Fahrenheit to Celsius first, then to Kelvin
                            val celsiusValue = fahrenheitToCelsius(finalTempValue)
                            val kelvinValue = celsiusToKelvin(celsiusValue).roundToInt()
                            "$kelvinValue ك°"
                        }
                    }
                    "fahrenheit" -> {
                        if (originalUnit == "س") {
                            // Convert from Celsius to Fahrenheit
                            val fahrenheitValue = celsiusToFahrenheit(finalTempValue).roundToInt()
                            "$fahrenheitValue ف°"
                        } else if (originalUnit == "ك") {
                            // Convert from Kelvin to Celsius, then to Fahrenheit
                            val celsiusValue = kelvinToCelsius(finalTempValue)
                            val fahrenheitValue = celsiusToFahrenheit(celsiusValue).roundToInt()
                            "$fahrenheitValue ف°"
                        } else {
                            // No conversion needed, just output in Arabic Fahrenheit
                            "$finalTempValue ف°"
                        }
                    }
                    else -> forecast.temp // Default case if the unit is unknown
                }

                // Return the ForecastFinal object for Arabic
                ForecastFinal(
                    temp = newTemp,
                    dt_txt = forecast.dt_txt,
                    icon = forecast.icon
                )

            } else {
                // English language logic
                val lastChar = tempString.lastOrNull()

                when (lastChar) {
                    'C', 'c' -> {
                        originalUnit = "C"
                        finalTempValue = tempString.substringBeforeLast("C", "").toDoubleOrNull() ?: 0.0
                    }
                    'K', 'k' -> {
                        originalUnit = "K"
                        finalTempValue = tempString.substringBeforeLast("K", "").toDoubleOrNull() ?: 0.0
                    }
                    'F', 'f' -> {
                        originalUnit = "F"
                        finalTempValue = tempString.substringBeforeLast("F", "").toDoubleOrNull() ?: 0.0
                    }
                }

                // Convert based on the requested unit
                val newTemp = when (tempUnit) {
                    "celsius" -> {
                        "$finalTempValue °C"
                    }
                    "kelvin" -> {
                        if (originalUnit == "C") {
                            // No conversion needed
                            "$finalTempValue °K"
                        } else if (originalUnit == "K") {
                            // No conversion needed, just output in English Kelvin
                            "$finalTempValue °K"
                        } else {
                            // Convert from Fahrenheit to Celsius, then to Kelvin
                            val celsiusValue = fahrenheitToCelsius(finalTempValue)
                            val kelvinValue = celsiusToKelvin(celsiusValue).roundToInt()
                            "$kelvinValue °K"
                        }
                    }
                    "fahrenheit" -> {
                        if (originalUnit == "C") {
                            // Convert from Celsius to Fahrenheit
                            val fahrenheitValue = celsiusToFahrenheit(finalTempValue).roundToInt()
                            "$fahrenheitValue °F"
                        } else if (originalUnit == "K") {
                            // Convert from Kelvin to Celsius, then to Fahrenheit
                            val celsiusValue = kelvinToCelsius(finalTempValue)
                            val fahrenheitValue = celsiusToFahrenheit(celsiusValue).roundToInt()
                            "$fahrenheitValue °F"
                        } else {
                            // No conversion needed, just output in English Fahrenheit
                            "$finalTempValue °F"
                        }
                    }
                    else -> forecast.temp // Default case if the unit is unknown
                }

                // Return the ForecastFinal object for English
                ForecastFinal(
                    temp = newTemp,
                    dt_txt = forecast.dt_txt,
                    icon = forecast.icon
                )
            }
        }
    }



    private fun fahrenheitToCelsius(fahrenheit: Double): Double {
        return (fahrenheit - 32) * 5 / 9
    }

    private fun kelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
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
}