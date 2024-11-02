package com.example.weatherproject.navbar.ui.favourite.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.pojos.WeatherFinal

class WeatherFinalDiffUtil : DiffUtil.ItemCallback<FullWeatherDetails>() {
    override fun areItemsTheSame(oldItem: FullWeatherDetails, newItem: FullWeatherDetails): Boolean {
        return oldItem.cityName == newItem.cityName
    }

    override fun areContentsTheSame(oldItem: FullWeatherDetails, newItem: FullWeatherDetails): Boolean {
        return oldItem == newItem
    }
}