package com.example.weatherproject.navbar.ui.favourite.viewmodel

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherproject.model.repository.remote.RemoteRepository
import com.example.weatherproject.model.ApiState
import com.example.weatherproject.model.Helpers.Conversions
import com.example.weatherproject.model.WeatherApiState
import com.example.weatherproject.model.pojos.DataBaseState
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.pojos.WeatherDb
import com.example.weatherproject.model.repository.local.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FavouriteViewModel(private var remoteRepository: RemoteRepository, private var repo: LocalRepository, private val context: Context) : ViewModel() {

    private val geocoder by lazy { Geocoder(context) }

    private val _currentWeatherFav = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    var currentWeatherFav: StateFlow<WeatherApiState> = _currentWeatherFav


    private var localList = MutableLiveData<List<WeatherDb>>(emptyList())
    public var cityFavList: LiveData<List<WeatherDb>> = localList

    //used for the data base
    private val readyLocalList = MutableStateFlow<DataBaseState>(DataBaseState.Loading)
    val readycityFavList: StateFlow<DataBaseState> = readyLocalList

    private val _weatherDetailsFavStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherDetailsFavStateFlow: StateFlow<ApiState> = _weatherDetailsFavStateFlow

     private var finalWeather = FullWeatherDetails()
    private var finalWeatherDB=WeatherDb()
    private var covMang = Conversions

    private var flag=0

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
                finalWeatherDB.setWeatherResponse(it ,languague,tempUnit,windUnit)
                var result = covMang.convertCurrentWeatherObj(it, tempUnit, windUnit, languague)
                finalWeather.setCurrentWeather(result)
                Log.i("DEBUG", "FINISHEDCURRENT")
                flag++
                if(flag==2)
                {
                    saveToDB()
                    flag=0
                }
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
                    finalWeatherDB.setForecastResponse(it)
                    var result = covMang.convert5Days3HoursObj(it, tempUnit, languague)
                    finalWeather.setForecast(result)
                    Log.i("DEBUG", "FINISHEDFORECAST")
                    flag++
                    if(flag==2)
                    {
                        saveToDB()
                        flag=0
                    }
                }
    }

     fun saveCityForecastWeatherDetails(
        location: Location,
        language: String,
        tempUnit: String,
        windUnit: String,
        favDetails:WeatherDb
    ) {
         finalWeatherDB.addressArabic = favDetails.addressArabic
         finalWeatherDB.addressEnglish = favDetails.addressEnglish
         finalWeatherDB.addressRomanion = favDetails.addressRomanion
         finalWeatherDB.countryArabic = favDetails.countryArabic
         finalWeatherDB.countryEnglish = favDetails.countryEnglish
         finalWeatherDB.countryRomanion = favDetails.countryRomanion
         finalWeatherDB.cityNameArabic = favDetails.cityNameArabic
         finalWeatherDB.cityNameEnglish = favDetails.cityNameEnglish
         finalWeatherDB.cityNameRomanian = favDetails.cityNameRomanian

//        finalWeather.address=favDetails.address
//        finalWeather.country=favDetails.country
         runBlocking(Dispatchers.IO) {
                 try {
                     val weatherDeferred = async {
                         saveCurrentWeatherFav(location, language, tempUnit, windUnit)
                         flag++
                     }

                     val forecastDeferred = async {
                         saveCurrentForecastFav(location, language, tempUnit, windUnit)
                         flag++
                     }
                     awaitAll(weatherDeferred, forecastDeferred)
                     while(flag != 2)
                         saveToDB()
                     flag=0


                 } catch (e: Exception) {
                 }

         }

         }

    fun saveToDB()
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavCityDetails(finalWeatherDB)
            Log.i("DEBUG", "DB")
        }
    }
     fun delFavCityWeatherDetails(lat:Double,long:Double)
    {
         viewModelScope.launch(Dispatchers.IO) {
             repo.deleteFavCityDetails(lat,long)
         }
    }
    fun getLocalFavForecastCities( language: String, tempUnit: String, windUnit: String,geocoder: Geocoder)=viewModelScope.launch(Dispatchers.IO){
        repo.getAllFavCities()
            ?.catch {
            }
            ?.collect{
                localList.postValue(it)
                if(it.isNotEmpty())
                    readyLocalList.value=DataBaseState.Success(Conversions.convertWeatherDbListToFullWeatherDetailsList(it,tempUnit,windUnit,language,geocoder))
            }
    }


}
class MyLocalFavCitiesFactory(private var remoteRepository: RemoteRepository, private var repo: LocalRepository, private val context: Context): ViewModelProvider.Factory{
    public override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(remoteRepository,repo,context) as T
    }
}