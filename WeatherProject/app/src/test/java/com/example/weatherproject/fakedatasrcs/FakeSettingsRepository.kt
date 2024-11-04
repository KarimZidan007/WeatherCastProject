package com.example.weatherproject.fakedatasrcs

import com.example.weatherproject.model.UserSettings
import com.example.weatherproject.model.repository.setting.ISettingRepository

class FakeSettingsRepository : ISettingRepository {

    private var userSettings: UserSettings = UserSettings(
        languagePreference = "en",
        temperatureUnit = "kelvin",
        locationPreference = "gps",
        windSpeedUnit = "meter_sec"
    )

    override fun getUserSettings(): UserSettings {
        return userSettings
    }

    override fun saveUserSettings(settings: UserSettings) {
        userSettings = settings
    }
}