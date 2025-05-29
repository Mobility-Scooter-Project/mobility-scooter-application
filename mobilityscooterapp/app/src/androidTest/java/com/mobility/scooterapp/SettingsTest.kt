package com.mobility.scooterapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

class SettingsTest {
    @Test
    fun setDarkMode_runsWithoutCrash() {
        SettingsUtil.setDarkMode(true)
        SettingsUtil.setDarkMode(false)
    }

}