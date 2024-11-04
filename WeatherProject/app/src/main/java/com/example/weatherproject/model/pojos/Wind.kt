package com.example.weatherproject.model.pojos

data class Wind(
    var speed: Double = 0.0,
    val deg: Int = 0,
    val gust: Double = 0.0
) {
    // Empty constructor
    constructor() : this(
        speed = 0.0,
        deg = 0,
        gust = 0.0
    )
}