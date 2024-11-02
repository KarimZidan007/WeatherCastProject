package com.example.labone.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherproject.model.pojos.FavCity
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {
    @Query("SELECT * FROM favouritecities_table")
     fun getAllFavCities(): Flow<List<FavCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavCity(city: FavCity):Long

    @Update
    suspend fun updateFavCity(city: FavCity)

    @Delete
    suspend fun deleteFavCity(city: FavCity):Int
}