package com.example.weatherproject.model.pojos

data class Main(
    var temp: Double,
    val feels_like: Double,
    var temp_min: Double,
    var temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)