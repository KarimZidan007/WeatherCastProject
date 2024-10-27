package com.example.weatherproject.model



data class UserSettings(
    var languagePreference: String = "en",
    var temperatureUnit: String = "kelvin",
    var locationPreference: String = "GPS",
    var windSpeedUnit: String = "meter_sec"
)