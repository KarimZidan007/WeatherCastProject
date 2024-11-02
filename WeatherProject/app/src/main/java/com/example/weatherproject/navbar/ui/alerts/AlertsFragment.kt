package com.example.weatherproject.navbar.ui.alerts

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherproject.databinding.FragmentAlertsBinding
import com.example.weatherproject.weathernotification.AlarmReceiver
import java.util.Calendar

class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val alertsViewModel = ViewModelProvider(this).get(AlertsViewModel::class.java)
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)

        // Initialize the UI
        val root: View = binding.root
        setupUi()

        return root
    }

    private fun setupUi() {
        binding.scheduleAlarmButton.setOnClickListener {
            onMealScheduleClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAlarmForWeather(context: Context, eventTime: Long, eventId: Long) {
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

    private fun cancelAlarmForWeather(context: Context, eventId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, eventId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Alarm canceled", Toast.LENGTH_SHORT).show()
    }

    private fun onMealScheduleClicked() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)

                        // Schedule the alarm for the selected date and time
                        setAlarmForWeather(requireContext(), calendar.timeInMillis, calendar.timeInMillis)
                    },
                    calendar[Calendar.HOUR_OF_DAY],
                    calendar[Calendar.MINUTE],
                    true
                )

                // Ensure the time picker shows future times if the selected date is today
                if (isToday(year, monthOfYear, dayOfMonth)) {
                    timePickerDialog.updateTime(
                        calendar[Calendar.HOUR_OF_DAY],
                        calendar[Calendar.MINUTE]
                    )
                }
                timePickerDialog.show()
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )

        // Ensure only future dates are selectable
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun isToday(year: Int, month: Int, day: Int): Boolean {
        val today = Calendar.getInstance()
        return today.get(Calendar.YEAR) == year &&
                today.get(Calendar.MONTH) == month &&
                today.get(Calendar.DAY_OF_MONTH) == day
    }
}
