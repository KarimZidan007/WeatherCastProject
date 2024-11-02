package com.example.weatherproject.navbar.ui.favourite.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mvvm_demo.model.repository.RemoteRepository
import com.example.weatherproject.model.ApiState
import com.example.weatherproject.model.Helpers.Conversions
import com.example.weatherproject.model.WeatherApiState
import com.example.weatherproject.model.pojos.FavCity
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.pojos.WeatherFinal
import com.example.weatherproject.model.repository.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel(private var remoteRepository: RemoteRepository, private var repo: LocalRepository) : ViewModel() {
    init{
        getLocalFavForecastCities()
    }

    private val _currentWeatherFav = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    var currentWeatherFav: StateFlow<WeatherApiState> = _currentWeatherFav


    private var localList = MutableLiveData<List<FullWeatherDetails>>(emptyList())
    public var cityFavList: LiveData<List<FullWeatherDetails>> = localList


    private val _weatherDetailsFavStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherDetailsFavStateFlow: StateFlow<ApiState> = _weatherDetailsFavStateFlow

    private var finalWeather = FullWeatherDetails()

    private var covMang = Conversions

    fun saveCurrentWeatherFav(
        location: Location,
        languague: String,
        tempUnit: String,
        windUnit: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        remoteRepository.getCurrentWeather(location, languague)
            .catch {
                _weatherDetailsFavStateFlow.value = ApiState.Failed(it)
            }
            .collect {
                var result = covMang.convertCurrentWeatherObj(it, tempUnit, windUnit, languague)
                finalWeather.setCurrentWeather(result)
                Log.i("NAMEEE", finalWeather.latitude.toString())
            }
    }

    fun saveCurrentForecastFav(
        location: Location,
        languague: String,
        tempUnit: String,
        windUnit: String
        )=viewModelScope.launch(Dispatchers.IO) {
        remoteRepository.get5day3hourForecast(location,languague)
                .catch {
                    _weatherDetailsFavStateFlow.value = ApiState.Failed(it)
                }
                .collect{
                    var result = covMang.convert5Days3HoursObj(it, tempUnit, languague)
                    finalWeather.setForecast(result)
                }
    }

    fun saveCityForecastWeatherDetails(
        location: Location,
        language: String,
        tempUnit: String,
        windUnit: String,
        favDetails:FullWeatherDetails
    ) {
        finalWeather.address=favDetails.address
        finalWeather.country=favDetails.country
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherDeferred = async {
                    saveCurrentWeatherFav(location, language, tempUnit, windUnit)
                }

                val forecastDeferred = async {
                    saveCurrentForecastFav(location, language, tempUnit, windUnit)
                }

                awaitAll(weatherDeferred, forecastDeferred)

                var result = repo.insertFavCityDetails(finalWeather)

            } catch (e: Exception) {
            }
        }
    }

     fun delFavCityWeatherDetails(city: FullWeatherDetails)
    {
         viewModelScope.launch(Dispatchers.IO) {
             repo.deleteFavCityDetails(city)
         }
    }
    fun getLocalFavForecastCities()=viewModelScope.launch(Dispatchers.IO){
        repo.getAllFavCities()
            .catch {
            }
            .collect{
                localList.postValue(it)
            }
    }


}
class MyLocalFavCitiesFactory(private var remoteRepository: RemoteRepository,private var repo: LocalRepository): ViewModelProvider.Factory{
    public override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(remoteRepository,repo) as T
    }
}