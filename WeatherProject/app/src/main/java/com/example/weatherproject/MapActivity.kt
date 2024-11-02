package com.example.weatherproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.weatherproject.model.pojos.FavCity
import com.example.weatherproject.model.pojos.FullWeatherDetails

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.gms.maps.model.Marker
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var currentMarker: Marker? = null
    private lateinit var selectedLatLng: LatLng
    private lateinit var currentLocale: Locale
    private lateinit var sharedPreferences :SharedPreferences
    private  var favCity:FullWeatherDetails= FullWeatherDetails()
    override fun attachBaseContext(newBase: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase)
        val languageCode = sharedPreferences.getString("language_preference", "en") ?: "en"
        currentLocale = Locale(languageCode)
        val context = ContextUtils.updateLocale(newBase, currentLocale)
        super.attachBaseContext(context)
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        val tvSelectedLocation = findViewById<TextView>(R.id.tv_selected_location)
        val btnSelectLocation = findViewById<Button>(R.id.btn_select_location)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val searchBar = findViewById<SearchView>(R.id.search_bar)
        btnSelectLocation.setOnClickListener {
            if (favCity.address.isNullOrEmpty() || favCity.country.isNullOrEmpty() ||
                favCity.latitude == 0.0 || favCity.longitude == 0.0) {
                Toast.makeText(this, "Incomplete location data. Please select a valid location.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent()
                intent.putExtra("fav", favCity)
                intent.putExtra("latitude", favCity.latitude)
                intent.putExtra("longitude", favCity.longitude)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val placeName = query.trim()
                if (placeName.isNotEmpty()) {
                    searchForLocation(placeName)
                } else {
                    Toast.makeText(
                        this@MapActivity,
                        "Please enter a valid place name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }

        })
    }



    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        val defaultLocation = LatLng(0.0, 0.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2f))

        mMap.setOnMapClickListener { latLng ->
            addOrUpdateMarker(latLng)
            selectedLatLng=latLng
            getCityNameFromCoordinates(latLng)
        }
    }

    private fun addOrUpdateMarker(latLng: LatLng) {
        currentMarker?.remove()

        currentMarker = mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
        currentMarker?.showInfoWindow()
        selectedLatLng = latLng
    }

    private fun getCityNameFromCoordinates(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5) // Increased number of results
            if (addresses != null && addresses.isNotEmpty()) {
                for (address in addresses) {
                    val cityName = address.locality ?: address.adminArea ?: address.featureName
                    val fullAddress = address.getAddressLine(0)
                    val countryName = address.countryName

                    if (!cityName.isNullOrEmpty() && !fullAddress.isNullOrEmpty() && !countryName.isNullOrEmpty() &&
                        latLng.latitude != 0.0 && latLng.longitude != 0.0) {
                        favCity.address = fullAddress
                        favCity.country = countryName
                        favCity.latitude = latLng.latitude
                        favCity.longitude = latLng.longitude

                        Toast.makeText(this, "City: $cityName, Address: $fullAddress, Country: $countryName", Toast.LENGTH_SHORT).show()
                        return // Exit once a valid address is found
                    }
                }
                Toast.makeText(this, "Complete address or valid data not found, please try again.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unknown location, please try a different spot.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to retrieve location details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchForLocation(placeName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            // Get a list of addresses matching the place name
            val addressList = geocoder.getFromLocationName(placeName, 5) // Increased the limit to find more options
            if (addressList != null && addressList.isNotEmpty()) {
                // Find the first address with a locality or fallback to the first address found
                val preferredAddress = addressList.find { it.locality != null } ?: addressList[0]

                // Log the preferred address for debugging
                Log.i("PLACE", preferredAddress.toString())
                val latLng = LatLng(preferredAddress.latitude, preferredAddress.longitude)
                addOrUpdateMarker(latLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), 1000, null)

                Toast.makeText(this, "Location: ${preferredAddress.locality ?: preferredAddress.featureName}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to find location", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.i("MapActivity", "Place: ${place.name}, ${place.id}, ${place.latLng}")
                // Add a marker at the selected place
                place.latLng?.let {
                    mMap.addMarker(MarkerOptions().position(it).title(place.name))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            }
        }
    }

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}
