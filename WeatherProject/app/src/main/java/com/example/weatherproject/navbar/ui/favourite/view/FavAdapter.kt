package com.example.weatherproject.navbar.ui.favourite.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherproject.databinding.FavCitiesCardBinding
import com.example.weatherproject.model.pojos.FullWeatherDetails

class FavAdapter(private val listener: (FullWeatherDetails) -> Unit ) : ListAdapter<FullWeatherDetails, FavAdapter.FavViewHolder>(WeatherFinalDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = FavCitiesCardBinding.inflate(inflater, parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val currentObj = getItem(position)

        holder.binding.country.text = currentObj.country
        holder.binding.fulladdress.text = currentObj.address

        holder.binding.card.setOnClickListener {
            listener.invoke(currentObj)
        }
    }

    class FavViewHolder(val binding: FavCitiesCardBinding) : RecyclerView.ViewHolder(binding.root)
}
