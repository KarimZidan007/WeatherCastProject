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
{
    constructor() : this(
        temp = 0.0,
        feels_like = 0.0,
        temp_min = 0.0,
        temp_max = 0.0,
        pressure = 0,
        sea_level = 0,
        grnd_level = 0,
        humidity = 0,
        temp_kf = 0.0
    )
}
