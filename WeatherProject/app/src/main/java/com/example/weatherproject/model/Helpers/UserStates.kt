package com.example.weatherproject.model.Helpers

import android.content.Context
import android.net.ConnectivityManager

object UserStates {
    fun checkConnectionState(activity:Context): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        return nInfo != null && nInfo.isAvailable && nInfo.isConnected
    }
}