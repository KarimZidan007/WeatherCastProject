
package com.example.weatherproject.model.pojos

import android.location.Geocoder
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherproject.model.WeatherResponse
import kotlinx.android.parcel.Parcelize
import java.util.Locale

@Parcelize
@Entity(tableName = "favforecast_table")
data class WeatherDb(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Auto-generated primary key
    var temp: Double = 0.0,
    var minTemp: Double = 0.0,
    var maxTemp: Double = 0.0,
    var pressure: Int = 0,
    var humidity: Int = 0,
    var windSpeed: Double = 0.0,
     var cityNameEnglish: String = "",
    var cityNameArabic: String = "",
    var cityNameRomanian: String = "",
    var addressEnglish: String = "",
    var addressArabic: String = "",
    var addressRomanion: String = "",
    var countryArabic: String = "",
    var countryEnglish: String = "",
    var countryRomanion: String = "",
    var descEnglish: String = "",
    var descArabic: String = "",
    var descRomanian: String = "",
    var icon: String = "",
    var lat_: Double = 0.0,
    var lng_: Double = 0.0,
    var weatherForecast: List<ForecastDB> = emptyList(),
    var lang: String = "",
    var tempUnit: String = "",
    var windUnit: String = ""
) : Parcelable {

    fun setWeatherResponse(
        weatherResponse: WeatherResponse,
        language: String,
        tempUnit: String,
        windUnit: String
    ) {
        this.icon = weatherResponse.weather[0].icon
        this.descEnglish = translateWeatherDescription(weatherResponse.weather[0].description,"en")

        // Translate weather descriptions
        this.descArabic = translateWeatherDescription(this.descEnglish, "ar")
        this.descRomanian = translateWeatherDescription(this.descEnglish, "ro")

        this.temp = weatherResponse.main.temp
        this.maxTemp = weatherResponse.main.temp_max
        this.minTemp = weatherResponse.main.temp_min
        this.humidity = weatherResponse.main.humidity
        this.windSpeed = weatherResponse.wind.speed
        this.pressure = weatherResponse.main.pressure


        this.lat_ = weatherResponse.coord.lat
        this.lng_ = weatherResponse.coord.lon
        this.lang = language
        this.tempUnit = tempUnit
        this.windUnit = windUnit
    }


    fun setForecastResponse(forecast: Root) {
        if (this.weatherForecast.isEmpty() || this.weatherForecast.size != forecast.list.size) {
            this.weatherForecast = List(forecast.list.size) { ForecastDB(0.0, "", "") }
        }

        // Update existing entries
        for (i in forecast.list.indices) {
            this.weatherForecast[i].apply {
                temp = forecast.list[i].main.temp
                icon = forecast.list[i].weather[0].icon
                dt_txt = forecast.list[i].dt_txt
            }
        }
    }
    private fun translateWeatherDescription(description: String, lang: String): String {
        return description
    }
}
