package com.example.weatherproject.navbar

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.weatherproject.ContextUtils
import com.example.weatherproject.R
import com.example.weatherproject.databinding.ActivityNavigationBinding
import com.example.weatherproject.locale.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale


class navigation : BaseActivity(){

    private lateinit var binding: ActivityNavigationBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var currentLocale: Locale
    override fun attachBaseContext(newBase: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase)
        val languageCode = sharedPreferences.getString("language_preference", "en") ?: "en"
        currentLocale = Locale(languageCode)
        val context = ContextUtils.updateLocale(newBase, currentLocale)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageCode = sharedPreferences.getString("language_preference", "en") ?: "en"
        val preferredLocale = Locale(languageCode)
        if (Locale.getDefault() != preferredLocale) {
            ContextUtils.updateLocale(this, preferredLocale)
            recreate()
        }
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.apply {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.setNavigationBarColor(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }



        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_navigation)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_alerts,
                R.id.navigation_favourite,
                R.id.navigation_settings
            )
        )
        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}