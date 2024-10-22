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
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)