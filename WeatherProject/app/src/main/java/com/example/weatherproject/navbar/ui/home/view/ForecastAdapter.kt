package com.example.weatherproject.navbar.ui.home.view

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherproject.databinding.ForecastViewholderBinding
import com.example.weatherproject.model.Helpers.Conversions
import com.example.weatherproject.model.pojos.ForecastFinal
import java.text.SimpleDateFormat

class ForecastAdapter(private var language: String = "en") : ListAdapter<ForecastFinal,ForecastAdapter.ViewHolder> (ForecastDiffUtil()){
    fun updateLang(lang:String)
    {
        language=lang
    }
    class ViewHolder(val binding: ForecastViewholderBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =  LayoutInflater.from(parent.context)
        val binding = ForecastViewholderBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentObj.dt_txt.toString())
        val calendar= Calendar.getInstance()
        calendar.time=date

        val hour=calendar.get(Calendar.HOUR_OF_DAY)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            when (language) {
                "ar" -> "ص"
                "ro" -> "AM"
                else -> "AM"
            }
        } else {
            when (language) {
                "ar" -> "م"
                "ro" -> "PM"
                else -> "PM"
            }
        }
        val icon = when (currentObj.icon) {
            "01d", "01n" -> "sunny"
            "02d", "02n" -> "cloudy_sunny"
            "03d", "03n" -> "cloudy_sunny"
            "04d", "04n" -> "cloudy"
            "09d", "09n" -> "rainy"
            "10d", "10n" -> "rainy"
            "11d", "11n" -> "storm"
            "13d", "13n" -> "snowy"
            "50d", "50n" -> "windy"
            else -> "sunny"
        }
        val hour12 = if (language == "ar") {
            Conversions.convertToArabicNumerals(hour.toString()) // Call your conversion function for Arabic
        } else {
            hour.toString()
        }

        fun convertToArabicNumerals(englishNumber: String): String {
            val englishToArabicMap = mapOf(
                '0' to '٠',
                '1' to '١',
                '2' to '٢',
                '3' to '٣',
                '4' to '٤',
                '5' to '٥',
                '6' to '٦',
                '7' to '٧',
                '8' to '٨',
                '9' to '٩'
            )

            return englishNumber.map { char ->
                englishToArabicMap[char] ?: char
            }.joinToString("")
        }
        val dayOfWeekName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> when (language) {
                "ar" -> "الأحد"
                "ro" -> "Duminică"
                else -> "Sunday"
            }
            Calendar.MONDAY -> when (language) {
                "ar" -> "الإثنين"
                "ro" -> "Luni"
                else -> "Monday"
            }
            Calendar.TUESDAY -> when (language) {
                "ar" -> "الثلاثاء"
                "ro" -> "Marți"
                else -> "Tuesday"
            }
            Calendar.WEDNESDAY -> when (language) {
                "ar" -> "الأربعاء"
                "ro" -> "Miercuri"
                else -> "Wednesday"
            }
            Calendar.THURSDAY -> when (language) {
                "ar" -> "الخميس"
                "ro" -> "Joi"
                else -> "Thursday"
            }
            Calendar.FRIDAY -> when (language) {
                "ar" -> "الجمعة"
                "ro" -> "Vineri"
                else -> "Friday"
            }
            Calendar.SATURDAY -> when (language) {
                "ar" -> "السبت"
                "ro" -> "Sâmbătă"
                else -> "Saturday"
            }
            else -> ""
        }
        val drawableResourceId : Int = holder.binding.root.resources.getIdentifier(
            icon,"drawable",holder.binding.root.context.packageName
        )
        Glide.with(holder.binding.root.context)
            .load(drawableResourceId)
            .into(holder.binding.pic)
        holder.binding.apply {
            namedaytext.text=dayOfWeekName
            hourtext.text="$hour12$amPm"
            rectemptext.text=currentObj.temp
        }

    }



}