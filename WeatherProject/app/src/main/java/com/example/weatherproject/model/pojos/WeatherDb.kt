package com.example.weatherproject.model.pojos

import androidx.room.PrimaryKey



data class WeatherDb(var temp:Double, var minTemp:Double, var maxTemp:Double, var pressure:Int, var humidity:Int, var windSpeed:Double, @PrimaryKey var cityName: String, var address:String, var country:String, var desc:String, var icon:String, var latitude :Double =0.0, var longitude:Double =0.0, var weatherForecast:List<ForecastFinal>) {
}