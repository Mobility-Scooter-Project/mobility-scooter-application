package com.mobility.mobilityscooterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
// Not implemented yet

class message_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerMessage, messages_page())
                .commitNow()
        }
    }
}