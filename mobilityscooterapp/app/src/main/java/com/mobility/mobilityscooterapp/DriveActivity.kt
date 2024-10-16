package com.mobility.mobilityscooterapp

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