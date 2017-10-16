@file:JvmName("NetworkUtils")


/**
 * @author lusinabrian
 * * Network utils will be used for the network checks in the application
 */
package com.librum.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager


/**
 * Method to check network availability
 * Using ConnectivityManager to check for IsNetwork Connection
 * @param context context in which to run this function. Will usually be run in
 * Activity/Fragment context
 */

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
}

/**
 * Checks if the wifi is enabled
 * */
fun isWifiEnabled(context: Context): Boolean {
    val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val ni = cm.activeNetworkInfo
    if (ni != null && ni.type == ConnectivityManager.TYPE_WIFI) {
        return true
    }
    return false
}

/**
 * checks if mobile data is enabled
 * */
fun isMobileDataEnabled(context: Context): Boolean {
    val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val ni = cm.activeNetworkInfo
    if (ni != null && ni.type == ConnectivityManager.TYPE_MOBILE) {
        return true
    }
    return false
}

/**
 * Checks if the device is connected to a Moja network
 * @param context The context in which to run this method
 * @return [String] SSID of the wifi
 * */
fun isConnectedToMojaNetwork(context: Context): Boolean {
    val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    if (manager.isWifiEnabled) {
        val wifiInfo = manager.connectionInfo
        if (wifiInfo != null) {
            val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
            if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                return wifiInfo.ssid == MOJA_SSID
            }
        }
    }
    return false
}
