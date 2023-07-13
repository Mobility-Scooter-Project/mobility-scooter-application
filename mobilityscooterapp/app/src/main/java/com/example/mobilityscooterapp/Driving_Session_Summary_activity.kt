package com.example.mobilityscooterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.VideoView

class Driving_Session_Summary_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driving_session_summary)

        val videoView = findViewById<VideoView>(R.id.videoView)

    }
}