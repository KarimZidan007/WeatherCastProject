package com.example.weatherproject.navbar.ui.alerts

import AlertAdapter
import LocalDataSrcImplementation
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherproject.database.AppDatabase
import com.example.weatherproject.database.event.EventDao
import com.example.weatherproject.databinding.FragmentAlertsBinding
import com.example.weatherproject.model.pojos.EventState
import com.example.weatherproject.model.repository.local.LocalRepository
import com.example.weatherproject.weathernotification.AlarmReceiver
import com.example.yourapp.MyDialogFragment
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerAdapter: AlertAdapter
    private lateinit var recyclerFav: RecyclerView
    private lateinit var alertsViewModel: AlertsViewModel
    private lateinit var alertsDAO:EventDao
    private lateinit var localSrc: LocalDataSrcImplementation
    private lateinit var localRepository: LocalRepository
    private lateinit var  factory:AlertsFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        alertsDAO = AppDatabase.getInstance(requireContext()).eventDao()
        localSrc=LocalDataSrcImplementation(alertsDAO,null)
        localRepository = LocalRepository(localSrc)
        factory= AlertsFactory(localRepository)
        alertsViewModel = ViewModelProvider(this,factory).get(AlertsViewModel::class.java)
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)

        val root: View = binding.root
        setupUi()
        setupBlurViews()
        setupRecyclerView()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            alertsViewModel.alertSetter.observe(viewLifecycleOwner, Observer {
                if(it.id!=null) {
                    Toast.makeText(requireContext(),"gerer",Toast.LENGTH_SHORT)
                    setAlarmForWeather(requireContext(),it.eventTime,it.id)
                }
            })
        }

        lifecycleScope.launch(Dispatchers.IO) {
            alertsViewModel.alertsList.collectLatest { state ->
                when (state) {

                    is EventState.Failed -> {


                    }

                    EventState.Loading -> {

                    }

                    is EventState.Success -> {
                        recyclerAdapter.submitList(state.events)
                    }
                }

            }
        }

    }
    private fun setupUi() {
        binding.scheduleAlarmButton.setOnClickListener {
            if (checkOverApplicationPermissions()) {
                //val dialog = SetNotificationDialogFragment()
                val dialog = MyDialogFragment()
                dialog.show(childFragmentManager, "MyDialogFragment")
            } else {
                enableOverOtherAppService()
                if (checkOverApplicationPermissions()) {
                  val dialog = MyDialogFragment()
                    dialog.show(childFragmentManager, "MyDialogFragment")
                }
            }
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

    private fun isToday(year: Int, month: Int, day: Int): Boolean {
        val today = Calendar.getInstance()
        return today.get(Calendar.YEAR) == year &&
                today.get(Calendar.MONTH) == month &&
                today.get(Calendar.DAY_OF_MONTH) == day
    }
    private fun setupRecyclerView() {
        recyclerAdapter = AlertAdapter()
        recyclerFav = binding.alertsRec
        recyclerFav.adapter = recyclerAdapter
        recyclerFav.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerFav.layoutManager = layoutManager
        recyclerAdapter.submitList(emptyList())
        setupSwipeToDelete()
    }
    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val event = recyclerAdapter.currentList[position]
                val builder = AlertDialog.Builder(context)
                builder.setMessage("If you proceed, you will remove ${event.title}")
                builder.setPositiveButton("Proceed") { dialog, _ ->
                    cancelAlarmForWeather(requireContext(),  event.id )
                    cancelAlarm(requireContext(), event.id.toInt())
                    alertsViewModel.deleteEvent(event)
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerFav)
    }
        private fun setupBlurViews() {
            activity?.let { activity ->
                val decorView = activity.window.decorView
                val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
                val windowBackground = decorView.background
                rootView?.let { root ->
                    binding.blueViewAlert.setupWith(root, RenderScriptBlur(requireContext()))
                        .setFrameClearDrawable(windowBackground)
                        .setBlurRadius(10f) // Set desired blur radius
                    binding.blueViewAlert.outlineProvider = ViewOutlineProvider.BACKGROUND
                    binding.blueViewAlert.clipToOutline = true
                }

        }
    }

    fun checkOverApplicationPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) and above
            return requireActivity().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    fun enableOverOtherAppService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Only request on Android 13 and above
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 2)
        }
    }

    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java) // Use the same receiver as when setting the alarm
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode, // Use the same request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel the alarm
        alarmManager.cancel(pendingIntent)

        pendingIntent.cancel()
    }

}
