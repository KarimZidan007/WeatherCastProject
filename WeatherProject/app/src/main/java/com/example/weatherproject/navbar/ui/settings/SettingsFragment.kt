package com.example.weatherproject.navbar.ui.settings

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.weatherproject.ContextUtils
import com.example.weatherproject.MapActivity
import com.example.weatherproject.R
import com.example.weatherproject.model.repository.SettingsRepository
import java.util.Locale

class SettingsFragment :  PreferenceFragmentCompat() {
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var location:Location
    private lateinit var sharedPreferences :SharedPreferences
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
         settingsRepository = SettingsRepository(sharedPreferences)
        var factory = SettingsFactory(settingsRepository)
        settingsViewModel = ViewModelProvider(requireActivity(),factory).get(SettingsViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        findPreference<ListPreference>("location_preference")
            ?.setOnPreferenceChangeListener { _, newValue ->
                settingsViewModel.updateLocationState(newValue.toString())
                if(newValue.toString()=="map")
                {
                    openMap()
                }
                true
            }
        findPreference<ListPreference>("language_preference")
            ?.setOnPreferenceChangeListener { _, newValue ->
                settingsViewModel.updateLanguage(newValue.toString())
                updateLanguage(newValue.toString())
                true
            }
    }

    fun openMap()
    {
        var intent: Intent = Intent(requireContext(), MapActivity::class.java)
        startActivityForResult(intent, 100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val latitude = data?.getDoubleExtra("latitude", 30.0)
            val longitude = data?.getDoubleExtra("longitude", 30.0)
             location = Location("selected_location").apply {
                latitude?.let { this.latitude = it }
                longitude?.let { this.longitude = it }
            }
            settingsViewModel.updateLastLocation(location)
        }
    }
    private fun updateLanguage(languageCode: String) {

        activity?.let {
            it.finish()
            val restartIntent = it.intent
            startActivity(restartIntent)
        }
    }
}