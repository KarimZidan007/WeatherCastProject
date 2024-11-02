package com.example.weatherproject.model.pojos

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "favouritecities_table")
@Parcelize
data class FavCity(var country :String , var address:String ,var long : Double , var lat:Double) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    constructor():this("","" ,0.0,0.0)
}

