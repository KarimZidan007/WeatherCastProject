package com.example.weatherproject.model.repository.setting

import com.example.weatherproject.model.UserSettings

interface ISettingRepository {
    fun getUserSettings(): UserSettings
    fun saveUserSettings(settings: UserSettings)
}