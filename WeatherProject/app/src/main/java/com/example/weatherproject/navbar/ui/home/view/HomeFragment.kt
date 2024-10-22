package com.example.weatherproject.navbar.ui.home.view

import ForecastRemoteDataSource
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.mvvm_demo.model.datasources.RemoteDataSrcImplementation
import com.example.mvvm_demo.model.repository.RemoteRepository
import com.example.weatherproject.R
import com.example.weatherproject.databinding.FragmentHomeBinding
import com.example.weatherproject.model.ApiState
import com.example.weatherproject.navbar.ui.home.HomeViewModel
import com.example.weatherproject.navbar.ui.home.MyWeather3hours5daysFactory
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.roundToInt

class HomeFragment : Fragment() {
    private lateinit var repository: RemoteRepository
    private lateinit var remoteSrc : RemoteDataSrcImplementation
    private lateinit var retroSrc : ForecastRemoteDataSource
    private var _binding: FragmentHomeBinding? = null
    private lateinit var  homeViewModel : HomeViewModel
    private val RecyclerAdapter: ForecastAdapter by lazy { ForecastAdapter()}

    private val calendar by lazy { Calendar.getInstance() }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.apply {
            citytxt.text="Giza"
            progressBar.visibility=View.VISIBLE
            detailLayout.visibility=View.INVISIBLE
        }
        retroSrc=ForecastRemoteDataSource()
        remoteSrc=RemoteDataSrcImplementation(retroSrc)
        repository=RemoteRepository(remoteSrc)
        var factory = MyWeather3hours5daysFactory(repository)
         homeViewModel = ViewModelProvider(this,factory).get(HomeViewModel::class.java)
        homeViewModel.getWeatherDetails5days3hours()


        return root
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
            homeViewModel.weatherDetailsStateFlow.collectLatest {
                when(it){
                    is ApiState.Success->{
                        //Current Weather
                        binding.apply {
                            progressBar.visibility=View.GONE
                            detailLayout.visibility=View.VISIBLE
                            citytxt.text=it.weatherDetails.city.name
                            statustxt.text= (it.weatherDetails.list.get(0).weather.get(0).main)
                            Windtext.text=it.weatherDetails.list.get(0).wind.speed.roundToInt().toString()+"-Km"
                            currentTempTxt.text=it.weatherDetails.list.get(0).main.temp.roundToInt().toString()+"°"
                            MaxTemotext.text=it.weatherDetails.list.get(0).main.temp_max.roundToInt().toString()
                            MinTempText.text=it.weatherDetails.list.get(0).main.temp_min.roundToInt().toString()
                            humidityUnit.text=it.weatherDetails.list.get(0).main.humidity.toString()+"%"
                            val drawable = if(isNightNow()) R.drawable.night_bg
                            else {
                                setDynamicWallper(it.weatherDetails.list.get(0).weather.get(0).icon)
                            }
                            bgimage.setImageResource(drawable)
                            setRainSnowEffect(it.weatherDetails.list.get(0).weather.get(0).icon)
                        }
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

                    is ApiState.Failed -> {
                    }

                    ApiState.Loading -> {
                        binding.progressBar.visibility=View.VISIBLE
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}