package com.example.weatherproject.model.pojos

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class ForecastDB (var temp:Double, var dt_txt: String, var icon:String) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    constructor():this(0.0,"","")
}