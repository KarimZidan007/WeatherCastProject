package com.example.weatherproject.weathernotification

import ForecastRemoteDataSource
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.mvvm_demo.model.datasources.RemoteDataSrcImplementation
import com.example.weatherproject.R
import com.example.weatherproject.model.Helpers.UserStates
import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.repository.remote.RemoteRepository
import com.example.weatherproject.model.repository.setting.SettingsRepository
import com.example.weatherproject.navbar.ui.alerts.AlertsFragment

import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.log

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var remoteRepository: RemoteRepository
    private lateinit var settingRepository: SettingsRepository
    private lateinit var remoteSrc: RemoteDataSrcImplementation
    private lateinit var retroSrc: ForecastRemoteDataSource
    private lateinit var sharedPreferences: SharedPreferences

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        // Initialize Repository
        retroSrc = ForecastRemoteDataSource()
        remoteSrc = RemoteDataSrcImplementation(retroSrc)
        remoteRepository = RemoteRepository(remoteSrc)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        settingRepository = SettingsRepository(sharedPreferences)
        // Handle notification permissions for Android Tiramisu and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("AlarmReceiver", "Notification permission not granted")
                return
            }
        }
        // Get data from the Intent
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        val city = intent.getStringExtra("city")
        val alarm = intent.getBooleanExtra("alarm", false)

        Log.i("AlarmReceiver", "Received intent with lat: $lat, lon: $lon, city: $city, alarm: $alarm")

        // Fetch weather data
        if (isOnline(context)) {
            GlobalScope.launch {
                val cityFlow = remoteRepository.getCurrentWeather(Location("current").apply {
                    latitude=lat
                    longitude=lon
                },settingRepository.getUserSettings().languagePreference)
                cityFlow.collect { data ->
                    showNotification(context, data, city ?: "Unknown City", alarm, lat, lon)
                    if (alarm) {
                        playAlarmSound(context)
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun showNotification(
        context: Context,
        currentWeather: WeatherResponse,
        city: String,
        alarm: Boolean,
        lat:Double,
        lon:Double
    ) {
        val alarmChannelId = "weather_alarm_channel"
        val notificationChannelId = "weather_notification_channel"

        // Create Notification Channels for Android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Alarm Channel
            val alarmChannel = NotificationChannel(
                alarmChannelId,
                "Weather Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for weather alarm notifications"
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
            }

            // Notification Channel
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Weather Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for regular weather notifications"
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            }

            // Register channels with Notification Manager
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(alarmChannel)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Intent to open the WeatherActivity when notification is tapped
        val notificationIntent = Intent(context, AlertsFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("city", city)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Stop Alarm" Intent
        val stopAlarmIntent = Intent(context, StopAlarmReceiver::class.java)
        stopAlarmIntent.putExtra("location",(lat+lon).toInt())
        val stopAlarmPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notificationBuilder = if (alarm){ NotificationCompat.Builder(context,alarmChannelId)
            .setSmallIcon(R.drawable.weather)
            .setContentTitle("Weather Alarm")
            .setContentText("Current weather: ${currentWeather.weather[0].description}, Temp: ${currentWeather.main.temp}")
            .setPriority(NotificationCompat.PRIORITY_MAX) // Use max for alarms
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.disablealarm, "Stop", stopAlarmPendingIntent) // Stop button
            .setAutoCancel(false)
            .setOngoing(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setFullScreenIntent(pendingIntent, true) // Full-screen intent for immediate visibility
        } else {
            // Regular Notification with short sound
            NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(R.drawable.weather)
                .setContentTitle("Weather Update")
                .setContentText("Current weather: ${currentWeather.weather[0].description}, Temp: ${currentWeather.main.temp}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun createNotificationIntent(context: Context, city: String): PendingIntent {
        val notificationIntent = Intent(context, AlertsFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("city", city)
        }

        return PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createStopPendingIntent(context: Context, lat: Double, lon: Double): PendingIntent {
        val stopAlarmIntent = Intent(context, StopAlarmReceiver::class.java).apply {
            putExtra("location", (lat + lon).toInt())
        }

        return PendingIntent.getBroadcast(
            context,
            0,
            stopAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun playAlarmSound(context: Context) {
        val alarmSoundUri: Uri = Uri.parse("android.resource://${context.packageName}/raw/alarm_sound")
        val mediaPlayer = MediaPlayer.create(context, alarmSoundUri)
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.start()
    }

    private fun isOnline(context: Context): Boolean {
        return  UserStates.checkConnectionState(context)
    }
    class StopAlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            cancelAlarm(context, intent.getIntExtra("location",0)) // Pass the request code used when setting the alarm

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(1) // Cancel the alarm notification
        }

        private fun cancelAlarm(context: Context, requestCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
