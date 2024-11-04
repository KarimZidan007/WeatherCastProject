package com.example.weatherproject.model.pojos

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class ForecastFinal (var temp:String, var dt_txt: String, var icon:String) : Parcelable {

    constructor():this("","","")
}