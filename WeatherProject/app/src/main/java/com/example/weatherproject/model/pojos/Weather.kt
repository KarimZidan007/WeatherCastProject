package com.example.weatherproject.model.pojos

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)