package com.example.weatherproject.model.pojos

data class Coord(
    val lat: Double,
    val lon: Double
) {
    // Empty constructor
    constructor() : this(lat = 0.0, lon = 0.0)
}