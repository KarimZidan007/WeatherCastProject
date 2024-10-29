package com.example.weatherproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.weatherproject.model.repository.SettingsRepository
import com.example.weatherproject.navbar.ui.settings.SettingsFactory
import com.example.weatherproject.navbar.ui.settings.SettingsViewModel
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

    override fun attachBaseContext(newBase: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase)
        val languageCode = sharedPreferences.getString("language_preference", "en") ?: "en"
        currentLocale = Locale(languageCode)
        val context = ContextUtils.updateLocale(newBase, currentLocale)
        super.attachBaseContext(context)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        val tvSelectedLocation = findViewById<TextView>(R.id.tv_selected_location)
        val btnSelectLocation = findViewById<Button>(R.id.btn_select_location)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btnSelectLocation.setOnClickListener {
            val intent = Intent()
            intent.putExtra("latitude", selectedLatLng.latitude)
            intent.putExtra("longitude", selectedLatLng.longitude)
            setResult(RESULT_OK, intent)
            finish()
        }
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
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                // Get locality (city name), adminArea (state), and countryName
                val cityName = addresses[0].locality ?: addresses[0].adminArea ?: addresses[0].countryName
                if (cityName != null) {
                    Toast.makeText(this, "City: $cityName", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Unknown location", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Unknown location", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to get city name", Toast.LENGTH_SHORT).show()
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
