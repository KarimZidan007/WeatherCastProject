package com.example.labone.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherproject.model.pojos.FavCity


@Database(entities = [FavCity::class],version=1)
abstract class RoomDataBase :RoomDatabase() {
abstract fun getFavCitiesDao():DAO
    companion object {
        @Volatile
        private var roomDataBase : RoomDataBase? = null
        fun getInstance(context: Context):RoomDataBase{
            return roomDataBase?:synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,RoomDataBase::class.java,"favcityDB").build()
                roomDataBase=instance
                instance
            }
        }
    }
}