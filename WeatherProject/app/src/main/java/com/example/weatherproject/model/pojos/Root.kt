package com.example.weatherproject.model.pojos

data class Root(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<Forecast>,
    val city: City
)
