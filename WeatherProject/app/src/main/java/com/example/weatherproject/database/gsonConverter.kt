package com.example.weatherproject.database
import androidx.room.TypeConverter
import com.example.weatherproject.model.pojos.ForecastFinal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class gsonConverter {
 private val gson = Gson()

    @TypeConverter
    fun fromForecastList(value: List<ForecastFinal>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toForecastList(value: String): List<ForecastFinal>? {
        val listType = object : TypeToken<List<ForecastFinal>>() {}.type
        return gson.fromJson(value, listType)
    }
}
