package com.example.weatherproject.model

import androidx.room.TypeConverter
import com.example.weatherproject.model.pojos.ForecastDB
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class gsonConverter {

    @TypeConverter
    fun fromForecastList(forecastList: List<ForecastDB>): String {
        return Gson().toJson(forecastList)
    }

    @TypeConverter
    fun toForecastList(forecastString: String): List<ForecastDB> {
        val listType = object : TypeToken<List<ForecastDB>>() {}.type
        return Gson().fromJson(forecastString, listType)
    }
}