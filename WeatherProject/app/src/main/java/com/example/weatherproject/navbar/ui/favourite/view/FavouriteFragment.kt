package com.example.weatherproject.navbar.ui.favourite.view

import ForecastRemoteDataSource
import LocalDataSrcImplementation
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labone.database.DAO
import com.example.labone.database.RoomDataBase
import com.example.mvvm_demo.model.datasources.RemoteDataSrcImplementation
import com.example.mvvm_demo.model.repository.RemoteRepository
import com.example.weatherproject.MapActivity
import com.example.weatherproject.R
import com.example.weatherproject.database.Weather.WeatherDAO
import com.example.weatherproject.database.Weather.WeatherRoomDataBase
import com.example.weatherproject.database.forecast.ForecastDAO
import com.example.weatherproject.database.forecast.FullForecastRoomDataBase
import com.example.weatherproject.databinding.FragmentFavouriteBinding
import com.example.weatherproject.model.pojos.FavCity
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.repository.LocalRepository
import com.example.weatherproject.model.repository.SettingsRepository
import com.example.weatherproject.navbar.ui.favourite.viewmodel.FavouriteViewModel
import com.example.weatherproject.navbar.ui.favourite.viewmodel.MyLocalFavCitiesFactory
import com.example.weatherproject.navbar.ui.home.HomeViewModel
import com.example.weatherproject.navbar.ui.home.OnFragmentInteractionListener
import com.example.weatherproject.navbar.ui.home.view.HomeFragment
import com.example.weatherproject.navbar.ui.settings.SettingsFactory
import com.example.weatherproject.navbar.ui.settings.SettingsViewModel

class FavouriteFragment : Fragment(), OnFragmentInteractionListener {

    private var _binding: FragmentFavouriteBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var localRepository: LocalRepository
    private lateinit var remoteRepository: RemoteRepository
    private lateinit var localSrc: LocalDataSrcImplementation
    private lateinit var remoteSrc: RemoteDataSrcImplementation
    private lateinit var retroSrc: ForecastRemoteDataSource
    lateinit var favDao: DAO
    private lateinit var favWeatherDao: WeatherDAO
    private lateinit var favForecastDao: ForecastDAO
    private val binding get() = _binding!!
    private lateinit var recyclerAdapter: FavAdapter
    private lateinit var recyclerFav: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var language: String
    private lateinit var settingRepository: SettingsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initializeComponents()
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.favCities.visibility = View.VISIBLE
        binding.imageView.visibility = View.VISIBLE
        binding.homeFragmentContainer.visibility = View.GONE
        setupRecyclerView()
        val addToFav = binding.imageView
        addToFav.setOnClickListener {
            openMap()
        }

        favouriteViewModel.cityFavList.observe(requireActivity()) { cityFavList ->
            if (cityFavList != null) {
                recyclerAdapter.submitList(cityFavList)
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel.isHomeFragmentVisible.observe(viewLifecycleOwner) { isVisible ->
            if (isVisible) {
                hideBackUIComponents()

            } else {
                showBackUIComponents()
            }
         }
    }
    override fun onResume() {
        super.onResume()

        if (childFragmentManager.backStackEntryCount == 0) {
            binding.favCities.visibility = View.VISIBLE
            binding.imageView.visibility = View.VISIBLE
            binding.homeFragmentContainer.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun openMap() {
        var intent: Intent = Intent(requireContext(), MapActivity::class.java)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val favCity = data?.getParcelableExtra<FullWeatherDetails>("fav")
            if (favCity != null) {
                val location = Location("customProvider")
                location.latitude = favCity.latitude
                location.longitude = favCity.longitude
                favouriteViewModel.saveCityForecastWeatherDetails(
                    location,
                    settingsViewModel.getLanguagueBasedOnPreference(),
                    settingsViewModel.getTemperatureBasedPreference(),
                    settingsViewModel.getWindSpeedBasedPreference(),
                    favCity
                )
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerAdapter = FavAdapter() {
            openHomeFragment(it)

        }
        recyclerFav = binding.favCities
        recyclerFav.adapter = recyclerAdapter
        recyclerFav.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerFav.layoutManager = layoutManager
        recyclerAdapter.submitList(emptyList())
        setupSwipeToDelete()
    }
    private fun openHomeFragment(favCity: FullWeatherDetails) {
        binding.homeFragmentContainer.visibility = View.VISIBLE
        binding.favCities.visibility=View.INVISIBLE
        binding.imageView.visibility=View.INVISIBLE
        val homeFragment = HomeFragment().apply {

            arguments = Bundle().apply {
                putParcelable("favCity", favCity)
                putBoolean("isFromFavourites", true)
            }
        }

        childFragmentManager.beginTransaction()
            .add(binding.homeFragmentContainer.id, homeFragment)
            .addToBackStack("HomeFragmentTransaction")
            .commit()

    }
    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val city = recyclerAdapter.currentList[position]
                favouriteViewModel.delFavCityWeatherDetails(city)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerFav)
    }
    private fun initializeComponents() {
        retroSrc = ForecastRemoteDataSource()
        remoteSrc = RemoteDataSrcImplementation(retroSrc)
        remoteRepository = RemoteRepository(remoteSrc)
        favDao = RoomDataBase.getInstance(requireContext()).getFavCitiesDao()
        favWeatherDao = WeatherRoomDataBase.getInstance(requireContext()).getFavCitiesWeatherDao()
        favForecastDao = FullForecastRoomDataBase.getInstance(requireContext()).getFavCitiesFullForecastDao()
        localSrc = LocalDataSrcImplementation(favDao,favWeatherDao,favForecastDao)
        localRepository = LocalRepository(localSrc)
        favouriteViewModel =
            ViewModelProvider(this, MyLocalFavCitiesFactory(remoteRepository, localRepository)).get(
                FavouriteViewModel::class.java
            )

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        settingRepository = SettingsRepository(sharedPreferences)
        var settingsFactory = SettingsFactory(settingRepository)
        settingsViewModel = ViewModelProvider(requireActivity(), settingsFactory).get(
            SettingsViewModel::class.java
        )
        language = settingsViewModel.getLanguagueBasedOnPreference()

    }
    fun showBackUIComponents() {
        binding.favCities.visibility = View.VISIBLE
        binding.imageView.visibility = View.VISIBLE
        binding.homeFragmentContainer.visibility = View.GONE
    }
    fun hideBackUIComponents() {
        binding.favCities.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.homeFragmentContainer.visibility = View.VISIBLE
    }
    override fun onHomeFragmentDestroyed() {
        Log.i("HomeFragment", "onHomeFragmentDestroyed: ")
        showBackUIComponents()
    }

}