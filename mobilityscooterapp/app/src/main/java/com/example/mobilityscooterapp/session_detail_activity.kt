package com.example.mobilityscooterapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.example.mobilityscooterapp.databinding.ActivitySessionDetailBinding
import java.io.File
import java.io.FileOutputStream

class session_detail_activity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        binding = ActivitySessionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val date = intent.getStringExtra("date")
        val startTime = intent.getStringExtra("start_time")
        val sessionLength = intent.getStringExtra("session_length")
        val encryptedVideoPath = intent.getStringExtra("encrypted_video_path")
        /*
        val estimate1 = intent.getStringExtra("estimate1")
        val estimate2 = intent.getStringExtra("estimate2")
         */

        // Set text views
        binding.textViewDate.text = getString(R.string.date_placeholder, date)
        binding.textViewStartTime.text = getString(R.string.start_time_placeholder, startTime)
        binding.SessionLength.text = getString(R.string.session_length_placeholder, sessionLength)
        /*
        binding.textViewEstimate.text = getString(R.string.estimate_placeholder, estimate1)
        binding.textViewEstimate2.text = getString(R.string.estimate_placeholder, estimate2)

         */

        if (encryptedVideoPath != null) {
            val encryptedFile = File(encryptedVideoPath)
            if (encryptedFile.exists()) {
                val decryptedFile = decryptFile(encryptedFile)

                // Set Video View
                val videoUri = Uri.fromFile(decryptedFile)
                binding.videoView.setVideoURI(videoUri)
                binding.videoView.setOnPreparedListener { mp ->
                    mp.isLooping = true
                    binding.videoView.start()
                }

                binding.videoView.setOnClickListener {
                    val watchVideo = Intent(this, video_view_activity::class.java).apply {
                        putExtra("video_path", decryptedFile.absolutePath)
                    }
                    startActivity(watchVideo)
                }
            } else {
                println("Error: encrypted file does not exist")
            }
        } else {
            println("Error: encrypted file path is null")
        }
    }

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