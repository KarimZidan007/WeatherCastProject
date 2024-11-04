package com.example.weatherproject.database.event

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherproject.model.pojos.EventAlerts
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: EventAlerts)

    @Delete
    suspend fun delete(event: EventAlerts)

    @Query("SELECT * FROM eventsAlerts ORDER BY eventTime ASC")
     fun getAllEvents(): Flow<List<EventAlerts>>
}
