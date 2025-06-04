package com.mobility.scooterapp
/**
 * VideoViewActivity
 * Handles getting the video form a path and then showing the video to the user
 *
 * Responsibilities:
 * - Handle using the url for videos
 * - Error handling for misinputs or user errors
 */
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.mobility.scooterapp.databinding.ActivityVideoViewBinding

class VideoViewActivity : AppCompatActivity() {

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


