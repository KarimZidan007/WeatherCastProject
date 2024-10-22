package com.example.weatherproject.navbar.ui.home

import ForecastRemoteDataSource
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mvvm_demo.model.repository.RemoteRepository
import com.example.weatherproject.model.ApiState
import com.example.weatherproject.model.pojos.Root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private var repo:RemoteRepository) : ViewModel() {

    private val _weatherDetailsStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherDetailsStateFlow: StateFlow<ApiState> = _weatherDetailsStateFlow
     fun getWeatherDetails5days3hours()=viewModelScope.launch(Dispatchers.IO) {
            repo.get5day3hourForecast()
                .catch {
                    _weatherDetailsStateFlow.value=ApiState.Failed(it)
                    Log.i("NAMEEE", "Failed")

                }
                .collect{
                    _weatherDetailsStateFlow.value=ApiState.Success(it)
                    Log.i("NAMEEE", "Success")
                }
    }
}
class MyWeather3hours5daysFactory(private var repo: RemoteRepository): ViewModelProvider.Factory{
    public override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}