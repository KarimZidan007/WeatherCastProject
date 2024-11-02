package com.example.weatherproject.model.pojos

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "favforecast_table")
data class FullWeatherDetails(var temp:String, var minTemp:String, var maxTemp:String, var pressure:String, var humidity:String, var windSpeed:String, @PrimaryKey var cityName: String,var address:String,var country:String, var desc:String, var icon:String, var latitude :Double =0.0, var longitude:Double =0.0, var weatherForecast:List<ForecastFinal>): Parcelable
{
    constructor() : this("", "", "", "", "","", "", "", "", "","", 0.0, 0.0, emptyList())
    fun setCurrentWeather(weather:WeatherFinal)
    {
        this.temp=weather.temp
        this.maxTemp=weather.maxTemp
        this.minTemp=weather.minTemp
        this.pressure=weather.pressure
        this.humidity=weather.humidity
        this.windSpeed=weather.windSpeed
        this.cityName=weather.cityName
        this.desc=weather.desc
        this.icon=weather.icon
        this.latitude=weather.latitude
        this.longitude=weather.longitude
    }
    fun setForecast(forecast:List<ForecastFinal>)
    {
        weatherForecast=forecast
    }
}
