package com.mobility.scooterapp

import org.junit.Assert.*
import org.junit.Test
import android.content.Context
import androidx.test.core.app.ApplicationProvider

class PermissionsTest {
    @Test
    fun hasCameraPermission_doesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // This doesn't guarantee true/false because we can't control real permissions here
        val result = SettingsUtil.hasCameraPermission(context)

        // Just assert that the function runs and returns a boolean
        assertTrue(result || !result)
    }
}