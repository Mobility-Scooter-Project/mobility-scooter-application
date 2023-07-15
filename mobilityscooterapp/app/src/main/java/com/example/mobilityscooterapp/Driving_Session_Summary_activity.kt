package com.example.mobilityscooterapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Driving_Session_Summary_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driving_session_summary)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val playPauseButton = findViewById<Button>(R.id.play_pause_button)


        // Get the path of the encrypted video file from the intent
        val encryptedFilePath = intent.getStringExtra("encrypted_video_path")

        if (encryptedFilePath != null) {
            val encryptedFile = File(encryptedFilePath)
            if (encryptedFile.exists()) {
                val decryptedFile = decryptFile(encryptedFile)

                // Play the decrypted video
                videoView.setVideoURI(Uri.fromFile(decryptedFile))
                playPauseButton.setOnClickListener {
                    if (videoView.isPlaying) {
                        videoView.pause()
                        playPauseButton.text = getString(R.string.start)
                    } else {
                        videoView.start()
                        playPauseButton.text = getString(R.string.pause)
                    }
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



