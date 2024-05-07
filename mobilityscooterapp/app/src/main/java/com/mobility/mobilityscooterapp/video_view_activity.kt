package com.mobility.mobilityscooterapp

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.mobility.mobilityscooterapp.databinding.ActivityVideoViewBinding

class video_view_activity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView2.setOnClickListener {
            finish()
        }

        // Get the path of the video file from the intent
        val videoPath = intent.getStringExtra("video_path")
        val uri = Uri.parse(videoPath)

        binding.videoView.setVideoURI(uri)

        val mediaController = MediaController(this)
        binding.videoView.setMediaController(mediaController)
        mediaController.setAnchorView(binding.videoView)

        binding.videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true

            binding.videoView.start()
        }
    }
}


