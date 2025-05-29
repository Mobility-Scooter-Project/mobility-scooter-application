package com.mobility.scooterapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

object SettingsUtil {
    /**
     * Returns camera permissions data from app
     *
     * @return true if user allowed app to access camera permissions, other wise returns false
     */
    public fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    /**
     * Called everytime the app is running and will be used based off app's preferences
     *
     * This method checks whether the dark mode feature is enabled
     *
     * @param isDarkMode checks the dark mode feature is activated or not within the app
     */
    public fun setDarkMode(isDarkMode: Boolean) {
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}