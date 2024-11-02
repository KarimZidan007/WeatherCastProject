package com.example.weatherproject.database.forecast

import android.content.Context
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherproject.database.gsonConverter
import com.example.weatherproject.model.pojos.FullWeatherDetails


@Database(entities = [FullWeatherDetails::class],version=1)
@TypeConverters(gsonConverter::class)
abstract class FullForecastRoomDataBase : RoomDatabase() {
    abstract fun getFavCitiesFullForecastDao(): ForecastDAO
    companion object {
        @Volatile
        private var fullForecastRoomDataBase : FullForecastRoomDataBase? = null
        fun getInstance(context: Context): FullForecastRoomDataBase {
            return fullForecastRoomDataBase?:synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    FullForecastRoomDataBase::class.java,"FavFullForecastDB").build()
                fullForecastRoomDataBase=instance
                instance
            }
        }
    }
}