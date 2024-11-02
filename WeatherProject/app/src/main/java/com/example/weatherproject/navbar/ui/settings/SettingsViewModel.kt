package com.example.weatherproject.navbar.ui.settings


import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvm_demo.model.repository.RemoteRepository
import com.example.weatherproject.model.UserSettings
import com.example.weatherproject.model.repository.SettingsRepository
import com.example.weatherproject.navbar.ui.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class SettingsViewModel (private var settingsRepository : SettingsRepository): ViewModel() {

    private val _isHomeFragmentVisible = MutableLiveData<Boolean>()
    val isHomeFragmentVisible: LiveData<Boolean>  = _isHomeFragmentVisible
    fun setHomeFragmentVisibility(isVisible: Boolean) {
        _isHomeFragmentVisible.value = isVisible
    }

    private val _settingsStateFlow = MutableStateFlow<UserSettings?>(UserSettings())
    val settingsStateFlow: StateFlow<UserSettings?> get() = _settingsStateFlow

    private val locationState = MutableStateFlow<String>("")
    val _locationState: StateFlow<String> = locationState

   private val lastLocation_ = MutableStateFlow<Location>(Location("default_provider").apply { latitude = 30.0; longitude = 30.0 })
    val lastLocation: StateFlow<Location> = lastLocation_

    private val languague = MutableStateFlow<String>("en")
    val _languague: StateFlow<String> = languague

    init {
        fetchSettings()
    }

    fun updateLocationState(newLocation: String) {
        locationState.value = newLocation
    }
    fun updateLastLocation(location:Location) {
        lastLocation_.value = location
    }
    fun updateLanguage(lang:String)
    {
        if(lang == "def")
        {
            languague.value= Locale.getDefault().toString().split("_").first() ?: "en"
        }
        else
        {
            languague.value=lang
        }
    }

    private fun fetchSettings() {
        _settingsStateFlow.value = settingsRepository.getUserSettings()
    }

    fun getWindSpeedBasedPreference() :String{
        return  settingsRepository.getUserSettings().windSpeedUnit
    }
    fun getTemperatureBasedPreference() :String{
        return  settingsRepository.getUserSettings().temperatureUnit
    }
    fun getLocationBasedOnPreference() : String {
        return  settingsRepository.getUserSettings().locationPreference
    }
    fun getLanguagueBasedOnPreference() : String{
        return  settingsRepository.getUserSettings().languagePreference

    }
}
class SettingsFactory( private var settingsRepository : SettingsRepository): ViewModelProvider.Factory {
    public override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(settingsRepository) as T
    }
}