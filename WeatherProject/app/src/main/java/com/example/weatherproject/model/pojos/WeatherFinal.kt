package com.example.weatherproject.model.pojos

data class WeatherFinal(var temp:String,var minTemp:String,var maxTemp:String,var pressure:String,var humidity:String,var windSpeed:String , var cityName :String , var desc:String ,var icon:String) {
    constructor():this("","","","","","" , "","","")
}