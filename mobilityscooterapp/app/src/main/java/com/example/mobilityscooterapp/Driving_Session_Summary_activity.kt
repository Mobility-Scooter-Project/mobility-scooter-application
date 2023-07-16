package com.example.mobilityscooterapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.example.mobilityscooterapp.databinding.ActivityDrivingSessionSummaryBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Driving_Session_Summary_activity : AppCompatActivity() {

    private lateinit var binding: ActivityDrivingSessionSummaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrivingSessionSummaryBinding.inflate(layoutInflater)

        binding.videoView.setOnClickListener{
            val watchVideo = Intent(this, video_view_activity::class.java)
            startActivity(watchVideo)
        }


        setContentView(R.layout.activity_driving_session_summary)

        val videoView = findViewById<VideoView>(R.id.videoView)


        // Get the path of the encrypted video file from the intent
        val encryptedFilePath = intent.getStringExtra("encrypted_video_path")


        if (encryptedFilePath != null) {
            val encryptedFile = File(encryptedFilePath)
            if (encryptedFile.exists()) {
                val decryptedFile = decryptFile(encryptedFile)



                // Play the decrypted video
                val videoUri = Uri.fromFile(decryptedFile)

                videoView.setVideoURI(videoUri)

                val handler = Handler(Looper.getMainLooper())
                val runnable = object : Runnable {
                    override fun run() {
                        if (videoView.currentPosition >= 5000) {
                            videoView.seekTo(1)
                        }
                        handler.postDelayed(this, 100)
                    }
                }

                videoView.setOnPreparedListener { mp ->
                    mp.isLooping = true

                    handler.postDelayed(runnable, 100)

                    videoView.start()
                }

            } else {
                println("error")
            }
        } else {
            println("error")
        }
    }

    //create a decrypt function when I retrieve encrypted video
    private fun decryptFile(encryptedFile: File): File {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val decryptedFile = File(filesDir, encryptedFile.name.replace("encrypted", "decrypted"))

        val encryptedFileInput = EncryptedFile.Builder(
            encryptedFile,
            this,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFileInput.openFileInput().use { inputStream ->
            FileOutputStream(decryptedFile).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }

        return decryptedFile
    }

}



