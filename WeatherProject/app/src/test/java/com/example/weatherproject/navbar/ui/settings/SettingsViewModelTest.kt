package com.example.weatherproject.navbar.ui.settings

import com.example.weatherproject.fakedatasrcs.FakeSettingsRepository
import com.example.weatherproject.model.UserSettings
import com.example.weatherproject.model.repository.setting.ISettingRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
class SettingsViewModelTest {
    private lateinit var settingsRepository: ISettingRepository
    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setup() {
        // Initialize the fake repository with default settings for testing
        settingsRepository = FakeSettingsRepository()
        settingsViewModel = SettingsViewModel(settingsRepository)
    }

    @Test
    fun getTempUnitBasedOnPreference_ReturnsCorrectTempUnit() {
        val expectedTemperature = "fahrenheit"
        settingsRepository.saveUserSettings(
            UserSettings(
                languagePreference = expectedTemperature,
                temperatureUnit = "fahrenheit",
                locationPreference = "network",
                windSpeedUnit = "miles/h"
            )
        )
        val actualTempUnit = settingsViewModel.getTemperatureBasedPreference()
        assertEquals(expectedTemperature, actualTempUnit)
    }

    @Test
    fun getLanguageBasedOnPreference_ReturnsCorrectLanguage() {
        val expectedLanguage = "en"
        settingsRepository.saveUserSettings(
            UserSettings(
                languagePreference = expectedLanguage,
                temperatureUnit = "fahrenheit",
                locationPreference = "network",
                windSpeedUnit = "miles/h"
            )
        )

        val actualLanguage = settingsViewModel.getLanguagueBasedOnPreference()
        assertEquals(expectedLanguage, actualLanguage)
    }
}