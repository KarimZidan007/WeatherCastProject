package com.example.weatherproject.navbar.ui.home.view

import ForecastRemoteDataSource
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvm_demo.model.datasources.RemoteDataSrcImplementation
import com.example.mvvm_demo.model.repository.RemoteRepository
import com.example.weatherproject.R
import com.example.weatherproject.databinding.FragmentHomeBinding
import com.example.weatherproject.model.ApiState
import com.example.weatherproject.model.WeatherApiState
import com.example.weatherproject.model.repository.SettingsRepository
import com.example.weatherproject.navbar.ui.home.HomeViewModel
import com.example.weatherproject.navbar.ui.home.MyWeather3hours5daysFactory
import com.example.weatherproject.navbar.ui.settings.SettingsFactory
import com.example.weatherproject.navbar.ui.settings.SettingsViewModel
import com.github.matteobattilana.weather.PrecipType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.math.roundToInt

class HomeFragment : Fragment() {
    private var LOCATION_PERMISSION_REQUEST_CODE = 5005
    private lateinit var remoteRepository: RemoteRepository
    private lateinit var settingRepository:SettingsRepository
    private lateinit var remoteSrc : RemoteDataSrcImplementation
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var retroSrc : ForecastRemoteDataSource
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentHomeBinding
    private lateinit var  homeViewModel : HomeViewModel
    private val RecyclerAdapter: ForecastAdapter by lazy { ForecastAdapter()}
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private var timer: Timer? = null
    private lateinit var fusedClient:FusedLocationProviderClient
    private val calendar by lazy { Calendar.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding.apply {
            citytxt.text="Giza"
            progressBar.visibility=View.VISIBLE
            detailLayout.visibility=View.INVISIBLE
            tempo.visibility=View.INVISIBLE
        }
        retroSrc=ForecastRemoteDataSource()
        remoteSrc=RemoteDataSrcImplementation(retroSrc)
        remoteRepository=RemoteRepository(remoteSrc)
        var factory = MyWeather3hours5daysFactory(remoteRepository)
        //creating homeViewModel
         homeViewModel = ViewModelProvider(this,factory).get(HomeViewModel::class.java)
        //creating SettingsViewModel
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        settingRepository= SettingsRepository(sharedPreferences)
        var settingsFactory = SettingsFactory(settingRepository)
        settingsViewModel = ViewModelProvider(requireActivity(),settingsFactory).get(SettingsViewModel::class.java)
        val locationSource = settingsViewModel.getLocationBasedOnPreference()
        when(locationSource){
            "gps"-> {
                fusedClient=LocationServices.getFusedLocationProviderClient(requireContext())
                checkLocationPermissions()
            }
            "map"->{
                homeViewModel.getWeatherDetails5days3hours(settingsViewModel.lastLocation.value,
                    settingsViewModel.getLanguagueBasedOnPreference(),settingsViewModel.getTemperatureBasedPreference()
                )
                homeViewModel.getCurrentWeather(settingsViewModel.lastLocation.value,
                    settingsViewModel.getLanguagueBasedOnPreference(),settingsViewModel.getTemperatureBasedPreference(),settingsViewModel.getWindSpeedBasedPreference()
                )
            }
        }
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            var radius=10f
            var decorView=it.window.decorView
            val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
            val windowBackground=decorView.background
            rootView?.let {
                binding.blueView.setupWith(it, RenderScriptBlur(requireContext()))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                binding.blueView.outlineProvider=ViewOutlineProvider.BACKGROUND
                binding.blueView.clipToOutline=true
            }
        }
        lifecycleScope.launch {
            homeViewModel.currentWeather.collectLatest {
                when (it) {
                    is WeatherApiState.Success -> {
                        //Current Weather
                        binding.apply {
                            progressBar.visibility = View.GONE
                            detailLayout.visibility = View.VISIBLE
                            tempo.visibility = View.VISIBLE
                            datetime.text = dateFormat.format(Date())
                            PressureValue.text = it.currentWeather.pressure
                            citytxt.text = it.currentWeather.cityName
                            statustxt.text = (it.currentWeather.desc)
                            Windtext.text =
                                it.currentWeather.windSpeed
                            currentTempTxt.text =
                                it.currentWeather.temp
                            MaxTemotext.text =
                                it.currentWeather.maxTemp
                            MinTempText.text =
                                it.currentWeather.minTemp
                            humidityUnit.text = it.currentWeather.humidity
                            val drawable = if (isNightNow()) R.drawable.night_bg
                            else {
                                setDynamicWallper(it.currentWeather.icon)
                            }
                            bgimage.setImageResource(drawable)
                            setRainSnowEffect(it.currentWeather.icon)
                        }
                    }

                    is WeatherApiState.Failed -> {
                    }

                    WeatherApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
        lifecycleScope.launch {
        homeViewModel.weatherDetailsStateFlow.collectLatest {
                when(it)
                {
                    is ApiState.Failed -> {

                    }
                    ApiState.Loading ->   binding.progressBar.visibility=View.VISIBLE

                    is ApiState.Success -> {
                        //ForeCast
                        binding.apply {
                            blueView.visibility=View.VISIBLE
                            RecyclerAdapter.submitList(it.weatherDetails.list)
                            forecastView.apply {
                                layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                                adapter=RecyclerAdapter
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Get the current date and time
                val currentDateTime = dateFormat.format(Date())
                requireActivity().runOnUiThread {
                    binding.datetime.text = currentDateTime
                }
            }
        }, 0, 60000)
    }
    override fun onStop() {
        super.onStop()
        timer?.cancel()
        timer = null
    }
    private fun isNightNow() : Boolean
    {
        return calendar.get(Calendar.HOUR_OF_DAY)>=10
    }
    private fun setDynamicWallper(icon:String):Int
    {
       return when(icon.dropLast(1))
       {
           "01" ->{
               initWeatherView(PrecipType.CLEAR)
               R.drawable.snow_bg
           }
           "02","03","04" ->{
               initWeatherView(PrecipType.CLEAR)
               R.drawable.cloudy_bg
           }
           "09","10","11" ->{
               initWeatherView(PrecipType.RAIN)
               R.drawable.rainy_bg
           }
           "13" ->{
               initWeatherView(PrecipType.SNOW)
               R.drawable.snow_bg
           }
           "50"->{
               initWeatherView(PrecipType.CLEAR)
               R.drawable.haze_bg
           }
           else -> 0
       }
    }
    private fun setRainSnowEffect(icon:String)
    {
         when(icon.dropLast(1))
        {
            "01" ->{
                initWeatherView(PrecipType.CLEAR)

            }
            "02","03","04" ->{
                initWeatherView(PrecipType.CLEAR)

            }
            "09","10","11" ->{
                initWeatherView(PrecipType.RAIN)

            }
            "13" ->{
                initWeatherView(PrecipType.SNOW)

            }
            "50"->{
                initWeatherView(PrecipType.CLEAR)

            }
            else -> 0
        }
    }

    private fun initWeatherView(type: PrecipType)
    {
        binding.weatherView.apply{
            setWeatherData(type)
            angle=-20
            emissionRate=100.0f
        }
    }

    private fun checkLocationPermissions()
    {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
           fusedClient.lastLocation.addOnSuccessListener {
               homeViewModel.getWeatherDetails5days3hours(it, settingsViewModel.getLanguagueBasedOnPreference(),settingsViewModel.getTemperatureBasedPreference())
               homeViewModel.getCurrentWeather(it, settingsViewModel.getLanguagueBasedOnPreference(),settingsViewModel.getTemperatureBasedPreference(),settingsViewModel.getWindSpeedBasedPreference())

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissions()
            } else {
                Snackbar.make(requireView(),"Location permission denied. Unable to fetch location.",
                    Snackbar.LENGTH_LONG
                ).setAction("Retry") {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }.show()
             }
        }
    }
//    fun openMap()
//    {
//        var intent:Intent = Intent(requireContext(),MapActivity::class.java)
//        startActivityForResult(intent, 100)
//    }

}
