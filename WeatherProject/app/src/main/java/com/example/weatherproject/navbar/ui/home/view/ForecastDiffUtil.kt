package com.example.weatherproject.navbar.ui.home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherproject.model.pojos.Forecast
import com.example.weatherproject.model.pojos.ForecastFinal

class ForecastDiffUtil : DiffUtil.ItemCallback<ForecastFinal>() {
    override fun areItemsTheSame(oldItem: ForecastFinal, newItem: ForecastFinal): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: ForecastFinal, newItem: ForecastFinal): Boolean {
        return oldItem==newItem
    }


}