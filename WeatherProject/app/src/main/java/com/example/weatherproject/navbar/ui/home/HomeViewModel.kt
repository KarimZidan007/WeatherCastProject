package com.example.weatherproject.navbar.ui.home


import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherproject.model.repository.remote.RemoteRepository
import com.example.weatherproject.model.ApiState
import com.example.weatherproject.model.Helpers.Conversions
import com.example.weatherproject.model.WeatherApiState
import com.example.weatherproject.model.repository.remote.IRemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
class HomeViewModel(private var remoteRepository: IRemoteRepository) : ViewModel() {

    private val _weatherDetailsStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherDetailsStateFlow: StateFlow<ApiState> = _weatherDetailsStateFlow

    private val _currentWeather = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    var currentWeather: StateFlow<WeatherApiState> = _currentWeather

    private var covMang = Conversions
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
                var result = covMang.convertCurrentWeatherObj(it, tempUnit, windUnit, languague)
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
                    var result = covMang.convert5Days3HoursObj(it, tempUnit, languague)
                    _weatherDetailsStateFlow.value = ApiState.Success(result)
                }
        }
}

class MyWeather3hours5daysFactory(private var remoteRepository: RemoteRepository): ViewModelProvider.Factory{
    public override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(remoteRepository) as T
    }


}
