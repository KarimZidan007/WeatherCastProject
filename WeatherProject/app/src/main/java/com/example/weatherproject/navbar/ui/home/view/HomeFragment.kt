package com.example.weatherproject.navbar.ui.home.view

import ForecastRemoteDataSource
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvm_demo.model.datasources.RemoteDataSrcImplementation
import com.example.weatherproject.model.repository.remote.RemoteRepository
import com.example.weatherproject.MapActivity
import com.example.weatherproject.R
import com.example.weatherproject.databinding.FragmentHomeBinding
import com.example.weatherproject.model.ApiState
import com.example.weatherproject.model.Helpers.UserStates
import com.example.weatherproject.model.WeatherApiState
import com.example.weatherproject.model.pojos.ForecastFinal
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.pojos.WeatherFinal
import com.example.weatherproject.model.repository.setting.SettingsRepository
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

class HomeFragment : Fragment() {
    private var LOCATION_PERMISSION_REQUEST_CODE = 5005
    private lateinit var remoteRepository: RemoteRepository
    private lateinit var settingRepository: SettingsRepository
    private lateinit var remoteSrc: RemoteDataSrcImplementation
    private lateinit var retroSrc: ForecastRemoteDataSource
    private lateinit var settingsViewModel: SettingsViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private val RecyclerAdapter: ForecastAdapter by lazy { ForecastAdapter() }
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private var timer: Timer? = null
    private lateinit var fusedClient: FusedLocationProviderClient
    private val calendar by lazy { Calendar.getInstance() }
    private lateinit var location: Location
    private lateinit var language: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding.apply {
            citytxt.text = "Giza"
            citytxt.setOnClickListener {
                openMap()
            }
            progressBar.visibility = View.VISIBLE
            tempo.visibility = View.INVISIBLE
            detailCard.visibility = View.INVISIBLE
            blueView.visibility = View.INVISIBLE
            blueViewThree.visibility = View.INVISIBLE
            weeklydetails.visibility = View.INVISIBLE
            citytxt.visibility=View.INVISIBLE
        }

        initializeComponents()
        val dayOfWeekName = when (calendar.get(android.icu.util.Calendar.DAY_OF_WEEK)) {
            android.icu.util.Calendar.SUNDAY -> when (language) {
                "ar" -> "الأحد"
                "ro" -> "Duminică"
                else -> "Sunday"
            }

            android.icu.util.Calendar.MONDAY -> when (language) {
                "ar" -> "الإثنين"
                "ro" -> "Luni"
                else -> "Monday"
            }

            android.icu.util.Calendar.TUESDAY -> when (language) {
                "ar" -> "الثلاثاء"
                "ro" -> "Marți"
                else -> "Tuesday"
            }

            android.icu.util.Calendar.WEDNESDAY -> when (language) {
                "ar" -> "الأربعاء"
                "ro" -> "Miercuri"
                else -> "Wednesday"
            }

            android.icu.util.Calendar.THURSDAY -> when (language) {
                "ar" -> "الخميس"
                "ro" -> "Joi"
                else -> "Thursday"
            }

            android.icu.util.Calendar.FRIDAY -> when (language) {
                "ar" -> "الجمعة"
                "ro" -> "Vineri"
                else -> "Friday"
            }

            android.icu.util.Calendar.SATURDAY -> when (language) {
                "ar" -> "السبت"
                "ro" -> "Sâmbătă"
                else -> "Saturday"
            }

            else -> ""
        }
        RecyclerAdapter.updateLang(language)
        binding.Today.text = dayOfWeekName

        getLocationHome()
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBlurViews()
        val args = arguments
        val isFromFavourites = args?.getBoolean("isFromFavourites", false) ?: false
        if (isFromFavourites) {
            val forecast: FullWeatherDetails = args?.getParcelable("favCity") ?: FullWeatherDetails()
            updateWeatherUI(convertToWeatherFinal(forecast))
            updateForecastUI(forecast.weatherForecast)

        } else {
            if(UserStates.checkConnectionState(requireContext()))
            {
                binding.apply {
                tempo.visibility = View.VISIBLE
                detailCard.visibility = View.VISIBLE
                blueView.visibility = View.VISIBLE
                blueViewThree.visibility = View.VISIBLE
                weeklydetails.visibility = View.VISIBLE
                    citytxt.visibility=View.VISIBLE
                }
                observeWeatherData()
            }
            else
            {
                binding.progressBar.visibility = View.INVISIBLE
                binding.bgimage.setImageResource(R.drawable.connection)
                binding.bgimage.scaleType=ImageView.ScaleType.CENTER_CROP

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsViewModel.setHomeFragmentVisibility(false)
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

    private fun isNightNow(): Boolean {
        return (calendar.get(Calendar.HOUR_OF_DAY) > 18 || calendar.get(Calendar.HOUR_OF_DAY) < 6)
        //  return (calendar.get(Calendar.HOUR_OF_DAY)>10)
    }

    private fun setDynamicWallper(icon: String): Int {
        return when (icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.clear
            }

            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloud
            }

            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
                R.drawable.rain
            }

            "13" -> {
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow
            }

            "50" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze
            }

            else -> 0
        }
    }

    private fun setRainSnowEffect(icon: String) {
        when (icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)

            }

            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)

            }

            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)

            }

            "13" -> {
                initWeatherView(PrecipType.SNOW)

            }

            "50" -> {
                initWeatherView(PrecipType.CLEAR)

            }

            else -> 0
        }
    }

    private fun initWeatherView(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (isAdded) {  // Ensure the fragment is added to the activity
                    homeViewModel.getWeatherDetails5days3hours(
                        location ?: Location("place").apply { latitude = 30.0; longitude = 30.0 },
                        settingsViewModel.getLanguagueBasedOnPreference(),
                        settingsViewModel.getTemperatureBasedPreference()
                    )
                    homeViewModel.getCurrentWeather(
                        location ?: Location("place").apply { latitude = 30.0; longitude = 30.0 },
                        settingsViewModel.getLanguagueBasedOnPreference(),
                        settingsViewModel.getTemperatureBasedPreference(),
                        settingsViewModel.getWindSpeedBasedPreference(),
                    )
                }
            }.addOnFailureListener { exception ->
                Log.e("HomeFragment", "Failed to get location", exception)
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
                Snackbar.make(
                    requireView(), "Location permission denied. Unable to fetch location.",
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

    fun openMap() {
        if(UserStates.checkConnectionState(requireContext()))
        {
            var intent: Intent = Intent(requireContext(), MapActivity::class.java)
            startActivityForResult(intent, 100)
        }
        else
        {
            view?.let {
                Snackbar.make(it, "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry") {
                        openMap() // Retry opening the map
                    }
                    .show()
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val latitude = data?.getDoubleExtra("latitude", 60.0)
            val longitude = data?.getDoubleExtra("longitude", 30.0)
            location = Location("selected_location").apply {
                latitude?.let { this.latitude = it }
                longitude?.let { this.longitude = it }
            }
            settingsViewModel.updateLastLocation(location)
            homeViewModel.getCurrentWeather(
                settingsViewModel.lastLocation.value,
                settingsViewModel.getLanguagueBasedOnPreference(),
                settingsViewModel.getTemperatureBasedPreference(),
                settingsViewModel.getWindSpeedBasedPreference(),
            )
        }
    }

    private fun displayWeatherForLocation(location: Location) {
        homeViewModel.getWeatherDetails5days3hours(
            location,
            settingsViewModel.getLanguagueBasedOnPreference(),
            settingsViewModel.getTemperatureBasedPreference()
        )
        homeViewModel.getCurrentWeather(
            location,
            settingsViewModel.getLanguagueBasedOnPreference(),
            settingsViewModel.getTemperatureBasedPreference(),
            settingsViewModel.getWindSpeedBasedPreference(),

        )
    }

    private fun updateWeatherUI(weather: WeatherFinal) {
        binding.apply {
            progressBar.visibility = View.GONE
            tempo.visibility = View.VISIBLE
            detailCard.visibility = View.VISIBLE
            datetime.text = dateFormat.format(Date())
            PressureValue.text = weather.pressure
            citytxt.text = weather.cityName
            statustxt.text = weather.desc
            Windtext.text = weather.windSpeed
            currentTempTxt.text = weather.temp
            MaxTemotext.text = weather.maxTemp
            MinTempText.text = weather.minTemp
            humidityUnit.text = weather.humidity

            val drawable = if (isNightNow()) R.drawable.night else setDynamicWallper(weather.icon)
            bgimage.setImageResource(drawable)
            setRainSnowEffect(weather.icon)
        }
    }

    private fun updateForecastUI(forecast: List<ForecastFinal>) {
        binding.apply {
            blueView.visibility = View.VISIBLE
            RecyclerAdapter.submitList(forecast)
            forecastView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = RecyclerAdapter
            }
        }

    }

    private fun getLocationHome()
    {
        val locationSource = settingsViewModel.getLocationBasedOnPreference()
        when (locationSource) {
            "gps" -> {
                fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
                checkLocationPermissions()
            }
            "map" -> {
                homeViewModel.getWeatherDetails5days3hours(
                    settingsViewModel.lastLocation.value,
                    settingsViewModel.getLanguagueBasedOnPreference(),
                    settingsViewModel.getTemperatureBasedPreference()
                )
                homeViewModel.getCurrentWeather(
                    settingsViewModel.lastLocation.value,
                    settingsViewModel.getLanguagueBasedOnPreference(),
                    settingsViewModel.getTemperatureBasedPreference(),
                    settingsViewModel.getWindSpeedBasedPreference(),
                )
            }
        }
    }

    private fun initializeComponents()
    {
        retroSrc = ForecastRemoteDataSource()
        remoteSrc = RemoteDataSrcImplementation(retroSrc)
        remoteRepository = RemoteRepository(remoteSrc)
        var factory = MyWeather3hours5daysFactory(remoteRepository)
        //creating homeViewModel
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        //creating SettingsViewModel
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        settingRepository = SettingsRepository(sharedPreferences)
        var settingsFactory = SettingsFactory(settingRepository)
        settingsViewModel =
            ViewModelProvider(requireActivity(), settingsFactory).get(SettingsViewModel::class.java)
        language = settingsViewModel.getLanguagueBasedOnPreference()
    }
    private fun setupBlurViews() {
        activity?.let { activity ->
            val radius = 10f
            val decorView = activity.window.decorView
            val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
            val windowBackground = decorView.background
            rootView?.let { root ->
                binding.blueView.setupWith(root, RenderScriptBlur(requireContext()))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                binding.blueView.outlineProvider = ViewOutlineProvider.BACKGROUND
                binding.blueView.clipToOutline = true

                binding.blueViewThree.setupWith(root, RenderScriptBlur(requireContext()))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                binding.blueViewThree.outlineProvider = ViewOutlineProvider.BACKGROUND
                binding.blueViewThree.clipToOutline = true
            }
        }
    }

    private fun observeWeatherData() {
        lifecycleScope.launch {
            homeViewModel.currentWeather.collectLatest { state ->
                when (state) {
                    is WeatherApiState.Success -> {
                        updateWeatherUI(state.currentWeather)
                    }
                    is WeatherApiState.Failed -> {
                        // Handle failure state (optional)
                    }
                    WeatherApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.weatherDetailsStateFlow.collectLatest { state ->
                when (state) {
                    is ApiState.Failed -> {
                        // Handle failure state (optional)
                    }
                    ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ApiState.Success -> {
                        // Forecast
                        updateForecastUI(state.weatherDetails)
                    }
                }
            }
        }
    }
    fun convertToWeatherFinal(fullDetails:FullWeatherDetails):WeatherFinal
    {
        return WeatherFinal(fullDetails.temp,fullDetails.minTemp,fullDetails.maxTemp,fullDetails.pressure,fullDetails.humidity,fullDetails.windSpeed,fullDetails.cityName,fullDetails.desc,fullDetails.icon)
    }

}
