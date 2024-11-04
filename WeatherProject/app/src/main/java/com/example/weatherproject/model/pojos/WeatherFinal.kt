package com.example.weatherproject.model.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey

data class WeatherFinal(var temp:String, var minTemp:String, var maxTemp:String, var pressure:String, var humidity:String, var windSpeed:String, var cityName: String = "", var desc:String, var icon:String, var latitude :Double =0.0, var longitude:Double =0.0) {

    constructor():this("","","","","","" , "","","")
}