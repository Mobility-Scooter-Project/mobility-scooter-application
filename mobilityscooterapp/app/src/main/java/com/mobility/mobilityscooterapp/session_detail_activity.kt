package com.mobility.mobilityscooterapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.mobility.mobilityscooterapp.databinding.ActivitySessionDetailBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.buffer
import okio.sink


class session_detail_activity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionDetailBinding
    private lateinit var decryptedFile: File

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
        /*
        val estimate1 = intent.getStringExtra("estimate1")
        val estimate2 = intent.getStringExtra("estimate2")
         */

        binding.textViewDate.text = getString(R.string.date_placeholder, date)
        binding.textViewStartTime.text = getString(R.string.start_time_placeholder, startTime)
        binding.SessionLength.text = getString(R.string.session_length_placeholder, sessionLength)
        /*
        binding.textViewEstimate.text = getString(R.string.estimate_placeholder, estimate1)
        binding.textViewEstimate2.text = getString(R.string.estimate_placeholder, estimate2)

         */
        if (encryptedFilePath != null && videoUrl != null) {
            val encryptedFile = File(encryptedFilePath)

            if (!encryptedFile.exists()) {
                downloadFile(videoUrl, encryptedFilePath)
            }
        }

        if (encryptedFilePath != null) {
            val encryptedFile = File(encryptedFilePath)

            if (encryptedFile.exists()) {
                val decryptedFile = decryptFile(encryptedFile)
                if (decryptedFile != null) {
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
                    Toast.makeText(this, "Decryption failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("SessionDetailActivity", "Video URL is null")
        }
        binding.backButton.setOnClickListener {
            deleteDecryptedFile()
            finish()
        }

    }

    private fun decryptFile(encryptedFile: File): File? {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        decryptedFile = File(filesDir, encryptedFile.name.replace("encrypted_", "decrypted_"))

        try {
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

    private fun downloadFile(url: String, path: String) {
        val request = Request.Builder().url(url).build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@session_detail_activity, "Failed to download file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val file = File(path)
                    val sink = file.sink().buffer()
                    sink.writeAll(response.body!!.source())
                    sink.close()

                    runOnUiThread {
                        Toast.makeText(this@session_detail_activity, "File downloaded successfully", Toast.LENGTH_SHORT).show()
                        // Here you can call a method to process the downloaded file
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@session_detail_activity, "Failed to download file", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


}