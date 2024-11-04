package com.example.weatherproject.model.repository.setting

import android.content.SharedPreferences
import com.example.weatherproject.model.UserSettings

class SettingsRepository(private val sharedPreferences: SharedPreferences) :ISettingRepository {

    override fun getUserSettings(): UserSettings {
        return UserSettings(
            languagePreference = sharedPreferences.getString("language_preference", "en") ?: "ar",
            temperatureUnit = sharedPreferences.getString("temp_unit_preference", "kelvin") ?: "kelvin",
            locationPreference = sharedPreferences.getString("location_preference", "gps") ?: "gps",
            windSpeedUnit = sharedPreferences.getString("wind_speed_preference", "meter_sec") ?: "meter_sec"
        )
    }

    override fun saveUserSettings(settings: UserSettings) {
        sharedPreferences.edit().apply {
            putString("language_preference", settings.languagePreference)
            putString("temp_unit_preference", settings.temperatureUnit)
            putString("location_preference", settings.locationPreference)
            putString("wind_speed_preference", settings.windSpeedUnit)
        }.apply()
    }
}

