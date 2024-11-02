package com.example.weatherproject.database.Weather

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherproject.model.pojos.WeatherFinal
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {

    @Query("SELECT * FROM favweather_table WHERE latitude = :lat AND longitude = :lng")
    fun getFavCityWeather(lat: Double, lng: Double): Flow<WeatherFinal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavCityWeather(city: WeatherFinal): Long

    @Update
    suspend fun updateFavCityWeather(city: WeatherFinal)

    @Delete
    suspend fun deleteFavCityWeather(city: WeatherFinal): Int
}