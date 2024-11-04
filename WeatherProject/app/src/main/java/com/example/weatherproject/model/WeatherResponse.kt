package com.example.weatherproject.model

import com.example.weatherproject.model.pojos.Clouds
import com.example.weatherproject.model.pojos.Coord
import com.example.weatherproject.model.pojos.Main
import com.example.weatherproject.model.pojos.Sys
import com.example.weatherproject.model.pojos.Weather
import com.example.weatherproject.model.pojos.Wind

data class WeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    var name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind){
    constructor() : this(
        base = "",
        clouds = Clouds(),
        cod = 0,
        coord = Coord(),
        dt = 0,
        id = 0,
        main = Main(),
        name = "",
        sys = Sys(),
        timezone = 0,
        visibility = 0,
        weather = emptyList(),
        wind = Wind()
    )
}





