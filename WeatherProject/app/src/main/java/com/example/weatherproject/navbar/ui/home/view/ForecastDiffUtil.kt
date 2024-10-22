package com.example.weatherproject.navbar.ui.home.view

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherproject.model.pojos.Forecast

class ForecastDiffUtil : DiffUtil.ItemCallback<Forecast>() {
    override fun areItemsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
        return oldItem==newItem
    }


}