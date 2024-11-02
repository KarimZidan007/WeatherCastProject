package com.example.weatherproject.weathernotification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.weatherproject.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("NAMEEE", "Notification permission not granted")
            }
        }
        val eventId = intent!!.getLongExtra("eventId", -1)
        showNotification(context!!, "Weather Reminder", "It's time to Discover Weather State")
    }

    fun showNotification(context: Context, title: String, message: String) {
        var notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "weather_channel_id"
            val channelName = "Meal Notifications"
            val channelDescription = "Notifications for meal reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription
            notificationManager.createNotificationChannel(channel)
            val builder: NotificationCompat.Builder =NotificationCompat.Builder(context, "weather_channel_id")
                .setSmallIcon(R.drawable.addtofav)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
            notificationManager.notify(1001, builder.build())
    }
}