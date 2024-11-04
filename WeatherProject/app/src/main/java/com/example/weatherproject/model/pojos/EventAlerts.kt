package com.example.weatherproject.model.pojos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eventsAlerts")
data class EventAlerts(
    var eventTime: Long,
    var title: String,
    var lat: Double,
    var lng: Double,
    var date: String,
    var alarm:Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    // Optional: Include a secondary constructor if needed, or you can use default values in primary constructor
    constructor() : this(0, "", 0.0, 0.0,"",false)

}