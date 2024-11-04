// MyDialogFragment.kt
package com.example.yourapp

import LocalDataSrcImplementation
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.weatherproject.MapActivity
import com.example.weatherproject.R
import com.example.weatherproject.database.AppDatabase
import com.example.weatherproject.database.event.EventDao
import com.example.weatherproject.databinding.DialogFragmentLayoutBinding
import com.example.weatherproject.model.pojos.EventAlerts
import com.example.weatherproject.model.pojos.WeatherDb
import com.example.weatherproject.model.repository.local.LocalRepository
import com.example.weatherproject.model.repository.setting.SettingsRepository
import com.example.weatherproject.navbar.ui.alerts.AlertsFactory
import com.example.weatherproject.navbar.ui.alerts.AlertsViewModel
import com.example.weatherproject.navbar.ui.settings.SettingsFactory
import com.example.weatherproject.navbar.ui.settings.SettingsViewModel
import com.example.weatherproject.weathernotification.AlarmReceiver
import java.util.*

class MyDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentLayoutBinding? = null
    private val binding get() = _binding!!
    private var dateFrom = Calendar.getInstance(Locale("en"))
    private var dateTo = Calendar.getInstance(Locale("en"))
    private var isSetFrom = false
    private var isSetTo = false
    private var numOfDaysTo: Long = 0
    private var numOfDayFrom: Long = 0
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingRepository: SettingsRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var location:Location
    private var event: EventAlerts =EventAlerts()
    private lateinit var alertsViewModel: AlertsViewModel
    private lateinit var alertsDAO: EventDao
    private lateinit var localSrc: LocalDataSrcImplementation
    private lateinit var localRepository: LocalRepository
    private lateinit var  factory:AlertsFactory
    private  var favCity: WeatherDb = WeatherDb()
    val calendar = Calendar.getInstance()
    var selectedYear = calendar.get(Calendar.YEAR)
    var selectedMonth = calendar.get(Calendar.MONTH)
    var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
    var selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
    var selectedMinute = calendar.get(Calendar.MINUTE)
    var lat:Double=0.0
    var lon:Double=0.0
    var country:String = "N/A"
    var city:String = "N/A"
    var countryArabic:String = "N/A"
    var cityArabic:String = "N/A"
    var time = false
    var alarm = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertsDAO = AppDatabase.getInstance(requireContext()).eventDao()
        localSrc=LocalDataSrcImplementation(alertsDAO,null)
        localRepository = LocalRepository(localSrc)
        factory= AlertsFactory(localRepository)
        alertsViewModel = ViewModelProvider(this,factory).get(AlertsViewModel::class.java)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        settingRepository = SettingsRepository(sharedPreferences)
        var settingsFactory = SettingsFactory(settingRepository)
        settingsViewModel =
            ViewModelProvider(requireActivity(), settingsFactory).get(SettingsViewModel::class.java)
        binding.radioGroup.check(R.id.radioButton1)

        // Set up "From Date" button click
        binding.imageView.setOnClickListener {
            val dateDialog = DatePickerDialog(requireContext())
            dateDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                // Set date in the Calendar instance
                dateFrom.set(Calendar.YEAR, year)
                dateFrom.set(Calendar.MONTH, month)
                dateFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                event.alarm=true

                // Show the time picker dialog after the date is set
                showTimePicker { hourOfDay, minute ->
                    dateFrom.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    dateFrom.set(Calendar.MINUTE, minute)
                    dateFrom.set(Calendar.SECOND, 0)
                    dateFrom.set(Calendar.MILLISECOND, 0)

                    // Convert the final Calendar date to milliseconds
                    val eventTimeInMillis = dateFrom.timeInMillis

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    val formattedDate = dateFormat.format(dateFrom.time)

                    // Set the event time in your event object
                    event.eventTime = eventTimeInMillis
                    event.date = formattedDate // Save the formatted date
                }
            }
            // Set the minimum date to the current date
            dateDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dateDialog.show()
        }

        // Set up "To Date" button click
        binding.imageView2.setOnClickListener {
            openMap()
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonOk.setOnClickListener {
            setAlarmForWeather(requireContext(),event.eventTime,(event.lat+event.lng).toLong(),event.lat,event.lng,event.title,event.alarm)
            Log.i("MyDialogFragment", event.title+" "+event.eventTime.toString()+" "+event.lat.toString()+" "+event.lng.toString())
            event.id=(event.lat+event.lng).toLong()
            alertsViewModel.insertEvent( event)
            dismiss()
        }
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.radioButton1.id -> {
                    alarm = true
                    event.alarm=true
                }

                binding.radioButton2.id -> {
                    alarm = false
                    event.alarm=false

                }
            }
        }
    }

    private fun showTimePicker(onTimeSet: (hourOfDay: Int, minute: Int) -> Unit) {
        val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            onTimeSet(hourOfDay, minute)
        }, dateFrom.get(Calendar.HOUR_OF_DAY), dateFrom.get(Calendar.MINUTE), true)

        timePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun openMap()
    {
        var intent: Intent = Intent(requireContext(), MapActivity::class.java)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val latitude = data?.getDoubleExtra("latitude", 30.0)
            val longitude = data?.getDoubleExtra("longitude", 30.0)
            var favCity = data?.getParcelableExtra<WeatherDb>("fav")
            location = Location("selected_location").apply {
                latitude?.let {
                    if (favCity != null) {
                        this.latitude = favCity.lat_
                        this.longitude= favCity.lng_
                        event.lat=favCity.lat_
                        lat=favCity.lat_
                        lon=favCity.lng_
                        city=favCity.cityNameEnglish
                        cityArabic=favCity.cityNameArabic
                        country=favCity.countryEnglish
                        countryArabic=favCity.countryArabic
                        event.lng=favCity.lng_
                        event.title=favCity.countryEnglish
                        country=favCity.countryEnglish
                        countryArabic=favCity.countryArabic
                        city=favCity.cityNameEnglish
                        cityArabic=favCity.cityNameArabic

                    }
                }
                longitude?.let { this.longitude = it }
                if (latitude != null) {
                    event.lat=latitude

                }
                if (longitude != null) {
                    event.lng=longitude
                }
            }
        }
    }
    private fun setAlarmForWeather(context: Context, eventTime: Long, eventId: Long,lat:Double , lon:Double ,city:String ,alarm:Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            }
        }
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("lat", lat)
            putExtra("lon", lon)
            putExtra("city", city)
            putExtra("alarm", alarm)
            putExtra("eventId", eventId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, eventId.toInt(), intent, PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            eventTime,
            pendingIntent
        )

        Toast.makeText(context, "Alarm set for the selected time", Toast.LENGTH_SHORT).show()
    }
}
