package com.example.weatherproject.navbar.ui.home.view

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherproject.databinding.ForecastViewholderBinding
import com.example.weatherproject.model.pojos.Forecast
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class ForecastAdapter : ListAdapter<Forecast,ForecastAdapter.ViewHolder> (ForecastDiffUtil()){
    class ViewHolder(val binding: ForecastViewholderBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =  LayoutInflater.from(parent.context)
        val binding = ForecastViewholderBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentObj.dt_txt.toString())
        val calendar= Calendar.getInstance()
        calendar.time=date
        val dayOfWeekName=when(calendar.get(Calendar.DAY_OF_WEEK))
        {
            1->"Sunday"
            2->"Monday"
            3->"Tuesday"
            4->"Wednesday"
            5->"Thursday"
            6->"Friday"
            7->"Saturday"
            else->""
        }
        val hour=calendar.get(Calendar.HOUR_OF_DAY)
        val amPm = if(hour<12)"am" else "pm"
        val hour12=calendar.get(Calendar.HOUR)
        val icon = when(currentObj.weather.get(0).icon.toString())
        {
            "01d","0n" -> "sunny"
            "02d","02n" -> "cloudy_sunny"
            "03d","03n" -> "cloudy_sunny"
            "04d","04n" -> "cloudy"
            "09d","09n" -> "rainy"
            "10d","10n" -> "rainy"
            "11d","11n" -> "storm"
            "13d","13n" -> "snowy"
            "50d","50n" -> "windy"
            else ->"sunny"
        }
        val drawableResourceId : Int = holder.binding.root.resources.getIdentifier(
            icon,"drawable",holder.binding.root.context.packageName
        )
        Glide.with(holder.binding.root.context)
            .load(drawableResourceId)
            .into(holder.binding.pic)
        holder.binding.apply {
            namedaytext.text=dayOfWeekName
            hourtext.text="$hour12$amPm"
            rectemptext.text=currentObj.main.temp.roundToInt().toString()+"°"
        }
    }



}