package com.example.weatherproject.database.forecast
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherproject.model.pojos.ForecastFinal
import com.example.weatherproject.model.pojos.FullWeatherDetails
import kotlinx.coroutines.flow.Flow


@Dao
interface ForecastDAO {

    @Query("SELECT * FROM favforecast_table ")
    fun getAllFavCityForecast(): Flow<List<FullWeatherDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavFullCityForecast(city: FullWeatherDetails): Long

    @Update
    suspend fun updateFavFullCityForecast(city: FullWeatherDetails)

    @Delete
    suspend fun deleteFavFullCityForecast(city: FullWeatherDetails): Int

}