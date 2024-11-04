package com.example.weatherproject.database.forecast
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.pojos.WeatherDb
import kotlinx.coroutines.flow.Flow


@Dao
interface ForecastDAO {

    @Query("SELECT * FROM favforecast_table ")
     fun getAllFavCityForecast(): Flow<List<WeatherDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavFullCityForecast(city: WeatherDb): Long

    @Update
    suspend fun updateFavFullCityForecast(city: WeatherDb)

    @Query("DELETE FROM favforecast_table WHERE lat_ = :lat AND lng_ = :lon")
    suspend fun deleteFavFullCityForecast(lat: Double, lon: Double): Int

}