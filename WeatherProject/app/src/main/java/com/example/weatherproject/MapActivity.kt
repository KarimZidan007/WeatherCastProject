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
import com.example.weatherproject.model.pojos.WeatherDb

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
    private  var favCity:WeatherDb= WeatherDb()
    private lateinit var cityName:String
    private lateinit var firstLineAddress:String
    private lateinit var countryName:String
    private lateinit var tvSelectedLocation:TextView
    private lateinit var btnSelectLocation:Button
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
         tvSelectedLocation = findViewById<TextView>(R.id.tv_selected_location)
         btnSelectLocation = findViewById<Button>(R.id.btn_select_location)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val searchBar = findViewById<SearchView>(R.id.search_bar)
        btnSelectLocation.setOnClickListener {
            if (favCity.addressEnglish.isNullOrEmpty() || favCity.countryEnglish.isNullOrEmpty())
                else {
                val intent = Intent()
                intent.putExtra("fav", favCity)
                intent.putExtra("latitude", favCity.lat_)
                intent.putExtra("longitude", favCity.lng_)
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
        val locales = listOf(Locale("ar"), Locale("en")) // Arabic and English only
        var favCityAddress = "No address found"
        var favCityName = mutableMapOf<String, String?>()
        var favAddress = mutableMapOf<String, String?>()
        var favCountryNameMap = mutableMapOf<String, String?>()

        for (locale in locales) {
            val geocoder = Geocoder(this, locale)
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]

                    // Get city name
                    val cityName = address.locality ?: address.adminArea ?: address.featureName
                    favCityName[locale.language] = cityName

                    // Get country name
                    favCountryNameMap[locale.language] = address.countryName

                    // Get address line
                    favAddress[locale.language] = address.getAddressLine(0)?.trim()

                    // Update the general address if available
                    favCityAddress = favAddress[locale.language] ?: favCityAddress
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Set the city, country, and address for each language
        favCity.addressArabic = favAddress["ar"] ?: "Unknown Address"
        favCity.addressEnglish = favAddress["en"] ?: "Unknown Address"
        favCity.addressRomanion = favAddress["en"] ?: "Unknown Address" // Set Romanian address to English

        favCity.cityNameArabic = favCityName["ar"] ?: "Unknown City"
        favCity.cityNameEnglish = favCityName["en"] ?: "Unknown City"
        favCity.cityNameRomanian = favCityName["en"] ?: "Unknown City" // Set Romanian city name to English

        favCity.countryArabic = favCountryNameMap["ar"] ?: "Unknown Country"
        favCity.countryEnglish = favCountryNameMap["en"] ?: "Unknown Country"
        favCity.countryRomanion = favCountryNameMap["en"] ?: "Unknown Country"

        favCity.lat_ = latLng.latitude
        favCity.lng_ = latLng.longitude

        if (favCityName.isNotEmpty()) {
            tvSelectedLocation.text = favCityAddress
        } else {
            // No valid address was found
            Toast.makeText(this, "Complete address or valid data not found, please try again.", Toast.LENGTH_SHORT).show()
        }
    }



    private fun searchForLocation(placeName: String) {
        val locales = listOf(Locale("ar"), Locale("en")) // Arabic and English only
        val geocoder = Geocoder(this)
        var favAddressMap = mutableMapOf<String, String?>() // To store addresses in different languages
        var favCityName = mutableMapOf<String, String?>()
        var favCountryNameMap = mutableMapOf<String, String?>()

        try {
            // Get a list of addresses matching the place name
            val addressList = geocoder.getFromLocationName(placeName, 5) // Increased the limit to find more options
            if (addressList != null && addressList.isNotEmpty()) {
                // Select the preferred address with a locality
                val preferredAddress = addressList.firstOrNull { it.locality != null } ?: addressList[0]

                // Loop through the locales to get addresses in each language
                for (locale in locales) {
                    // Create a geocoder for the current locale
                    val localizedGeocoder = Geocoder(this, locale)
                    val localizedAddress = localizedGeocoder.getFromLocation(preferredAddress.latitude, preferredAddress.longitude, 1)

                    // Get the address line and country name in the current locale
                    val addressLine = localizedAddress?.firstOrNull()?.getAddressLine(0)?.trim()
                    favAddressMap[locale.language] = addressLine ?: "Unknown Address"

                    // Store city and country names
                    favCityName[locale.language] = preferredAddress.locality ?: preferredAddress.featureName
                    favCountryNameMap[locale.language] = preferredAddress.countryName
                }

                // Log the preferred address
                Log.i("PLACE", preferredAddress.toString())
                val latLng = LatLng(preferredAddress.latitude, preferredAddress.longitude)
                addOrUpdateMarker(latLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), 1000, null)

                // Set values in favCity object
                favCity.lat_ = latLng.latitude
                favCity.lng_ = latLng.longitude

                favCity.addressArabic = favAddressMap["ar"] ?: "Unknown Address"
                favCity.addressEnglish = favAddressMap["en"] ?: "Unknown Address"
                favCity.addressRomanion = favCity.addressEnglish // Set Romanian address to English

                favCity.countryArabic = favCountryNameMap["ar"] ?: "Unknown Country"
                favCity.countryEnglish = favCountryNameMap["en"] ?: "Unknown Country"
                favCity.countryRomanion = favCity.countryEnglish // Set Romanian country name to English

                favCity.cityNameArabic = favCityName["ar"] ?: "Unknown City"
                favCity.cityNameEnglish = favCityName["en"] ?: "Unknown City"
                favCity.cityNameRomanian = favCity.cityNameEnglish // Set Romanian city name to English

                // Prepare the display message
                val displayAddress = "${favCity.addressEnglish} (${favCity.addressArabic})"
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
