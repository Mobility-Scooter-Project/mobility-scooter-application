package com.mobility.scooterapp
/**
 * DriveActivity
 * Handles user interactions related to driving mode in the app.
 *
 * Responsibilities:
 * - Manages UI elements for driving mode.
 * - Listens for user inputs and updates the UI accordingly.
 * - Handles necessary permissions and sensor interactions.
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DriveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerDrive, DriveStartPage())
                .commitNow()
        }
    }
}