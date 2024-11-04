package com.example.weatherproject.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.weatherproject.database.event.EventDao
import com.example.weatherproject.model.pojos.EventAlerts

@Database(entities = [EventAlerts::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "events_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
