package com.mobility.scooterapp
/**
 * SessionDetailActivity
 * Handles results from recordings
 *
 * Responsibilities:
 * - Manages decrypting and handling video file
 * - Handles downloading the videos
 */
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.mobility.scooterapp.databinding.ActivitySessionDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink

class SessionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionDetailBinding
    private lateinit var decryptedFile: File
    private lateinit var videoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        binding = ActivitySessionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val date = intent.getStringExtra("date")
        val startTime = intent.getStringExtra("start_time")
        val sessionLength = intent.getStringExtra("session_length")
        val encryptedFilePath = intent.getStringExtra("encryptedFilePath")
        val videoUrl = intent.getStringExtra("video_url")
        val estimateData = intent.getStringExtra("estimateData")

        binding.textViewDate.text = getString(R.string.date_placeholder, date)
        binding.textViewStartTime.text = getString(R.string.start_time_placeholder, startTime)
        binding.SessionLength.text = getString(R.string.session_length_placeholder, sessionLength)
        binding.textViewEstimate.text = getString(R.string.estimate_placeholder, estimateData)

        if (encryptedFilePath != null && videoUrl != null) {
            val encryptedFile = File(encryptedFilePath)

            if (!encryptedFile.exists()) {
                lifecycleScope.launch {
                    try{
                        val filePath = downloadFile(videoUrl, encryptedFilePath)
                        videoFile = File(filePath)
                        playVideo()
                    } catch (e: Exception) {
                        Log.e("Download", "Download failed: ${e.message}")
                    }
                }
            } else {
                try {
                    val encryptedFile = File(encryptedFilePath)
                    val decryptedFile = decryptFile(encryptedFile)

                    if (decryptedFile == null) {
                        encryptedFile.delete()
                        Toast.makeText(this, "Decryption failed", Toast.LENGTH_SHORT).show()
                    } else{
                        videoFile = decryptedFile
                        playVideo()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Decryption failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backButton.setOnClickListener {
            deleteDecryptedFile()
            deleteVideoFile()
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        deleteDecryptedFile()
        deleteVideoFile()
    }

    private fun playVideo() {
        if (videoFile.exists()) {
            val videoUri = Uri.fromFile(videoFile)
            binding.videoView.setVideoURI(videoUri)
            binding.videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                binding.videoView.start()
            }

            binding.videoView.setOnClickListener {
                val watchVideo = Intent(this, VideoViewActivity::class.java).apply {
                    putExtra("video_path", videoFile.absolutePath)
                }
                startActivity(watchVideo)
            }
        } else {
            Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decryptFile(encryptedFile: File): File? {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        decryptedFile = File(filesDir, encryptedFile.name.replace("encrypted_", "decrypted_"))

        try {
            val encryptedFileInput = EncryptedFile.Builder(
                encryptedFile,
                applicationContext,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            encryptedFileInput.openFileInput().use { inputStream ->
                decryptedFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            Log.e("SessionDetailActivity", "File decryption failed", e)
            return null
        }

        return decryptedFile
    }

    private fun deleteDecryptedFile() {
        if (::decryptedFile.isInitialized && decryptedFile.exists()) {
            decryptedFile.delete()
        }
    }

    private fun deleteVideoFile() {
        if (::videoFile.isInitialized && videoFile.exists()) {
            videoFile.delete()
        }
    }

    private suspend fun downloadFile(url: String, path: String): String {
        return withContext(Dispatchers.IO) {  // Run in background thread
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()

            try {
                val response = client.newCall(request).execute() // Synchronous request

                if (!response.isSuccessful) {
                    throw IOException("Failed to download file: ${response.code}")
                }

                val file = File(path)
                file.sink().buffer().use { sink ->
                    sink.writeAll(response.body!!.source()) // Write response body to file
                }

                return@withContext file.absolutePath // Return the downloaded file path
            } catch (e: Exception) {
                throw IOException("Download failed: ${e.message}", e) // Throw error if download fails
            }
        }
    }
}