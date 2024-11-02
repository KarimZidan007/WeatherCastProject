package com.example.weatherproject.database.Weather

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherproject.model.pojos.WeatherFinal


@Database(entities = [WeatherFinal::class],version=1)
abstract class WeatherRoomDataBase : RoomDatabase()  {
    abstract fun getFavCitiesWeatherDao(): WeatherDAO
    companion object {
        @Volatile
        private var weatherRoomDataBase : WeatherRoomDataBase? = null
        fun getInstance(context: Context): WeatherRoomDataBase {
            return weatherRoomDataBase?:synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    WeatherRoomDataBase::class.java,"FavWeatherDB").build()
                weatherRoomDataBase=instance
                instance
            }
        }
    }
}