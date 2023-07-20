package com.example.mobilityscooterapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.example.mobilityscooterapp.databinding.ActivityDrivingSessionSummaryBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File
import kotlinx.coroutines.*
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.ZoneOffset
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.util.Date

class Driving_Session_Summary_activity : AppCompatActivity() {

    private lateinit var binding: ActivityDrivingSessionSummaryBinding
    private lateinit var decryptedFile: File


    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrivingSessionSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the path of the encrypted video file from the intent
        val encryptedFilePath = intent.getStringExtra("encrypted_video_path")

        val date = intent.getStringExtra("date")
        val startTime = intent.getStringExtra("start_time")
        val sessionLength = intent.getStringExtra("session_length")

        val dateTextView = findViewById<TextView>(R.id.textViewDate)
        val startTimeTextView = findViewById<TextView>(R.id.textViewStartTime)
        val sessionLengthTextView = findViewById<TextView>(R.id.SessionLength)

        dateTextView.text = getString(R.string.date_placeholder, date)
        startTimeTextView.text = getString(R.string.start_time_placeholder, startTime)
        sessionLengthTextView.text = getString(R.string.session_length_placeholder, sessionLength)

        if (encryptedFilePath != null) {
            val encryptedFile = File(encryptedFilePath)

            /* This part is for the Database and firebase storage */
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val storage = Firebase.storage
            val storageRef = storage.reference
            val videoRef = storageRef.child("users/$userId/videos/${encryptedFile.name}")
            val db = Firebase.firestore
            val sessionDocRef = db.collection("users").document(userId!!).collection("sessions").document()

            /**/

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            val videoUploadTask = videoRef.putFile(Uri.fromFile(encryptedFile)).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                videoRef.downloadUrl
            }


            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            if (encryptedFile.exists()) {
                val decryptedFile = decryptFile(encryptedFile)

                // Create a thumbnail from the decrypted video
                val thumbnailFile = File(externalCacheDir, "${encryptedFile.name}_thumbnail.png")
                val fos = FileOutputStream(thumbnailFile)
                GlobalScope.launch(Dispatchers.IO) {
                    ThumbnailUtils.createVideoThumbnail(
                        decryptedFile.path,
                        MediaStore.Video.Thumbnails.MINI_KIND
                    )?.compress(Bitmap.CompressFormat.JPEG, 75, fos)
                    fos.close()
                }

                val thumbnailRef = storageRef.child("users/$userId/thumbnails/${thumbnailFile.name}")





                // Start video_view_activity with the path of decrypted video
                binding.videoView.setOnClickListener {
                    val watchVideo = Intent(this, video_view_activity::class.java).apply {
                        putExtra("video_path", decryptedFile.absolutePath)
                    }
                    startActivity(watchVideo)
                }

                // Play the decrypted video
                val videoUri = Uri.fromFile(decryptedFile)
                binding.videoView.setVideoURI(videoUri)

                val handler = Handler(Looper.getMainLooper())
                val runnable = object : Runnable {
                    override fun run() {
                        if (binding.videoView.currentPosition >= 5000) {
                            binding.videoView.seekTo(1)
                        }
                        handler.postDelayed(this, 100)
                    }
                }

                binding.videoView.setOnPreparedListener { mp ->
                    mp.isLooping = true
                    handler.postDelayed(runnable, 100)
                    binding.videoView.start()
                }

                /* This part is for the Database and firebase storage */
                val thumbnailUploadTask = thumbnailRef.putFile(Uri.fromFile(thumbnailFile)).continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    thumbnailRef.downloadUrl
                }
                Tasks.whenAllSuccess<Uri>(videoUploadTask, thumbnailUploadTask).addOnSuccessListener { urls ->

                    val videoLink = urls[0].toString() // 0-index is videoUploadTask
                    val thumbnailLink = urls[1].toString() // 1-index is thumbnailUploadTask
                    val dateTimeString = "$date $startTime" // Combine date and startTime


                    val sessionData = hashMapOf(
                        "date" to date,
                        "start_time" to startTime,
                        "session_length" to sessionLength,
                        "video_url" to videoLink,
                        "thumbnail_url" to thumbnailLink,
                        "dateTimeString" to dateTimeString
                    )

                    db.collection("users").document(userId!!).collection("sessions").document()
                        .set(sessionData)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully written!")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error writing document", e)
                        }
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "Error: ${exception.message}")
                }


                /* end */
            } else {
                println("Error: encrypted file does not exist")
            }
        } else {
            println("Error: encrypted file path is null")
        }

        binding.buttonNext.setOnClickListener {
            deleteDecryptedFile()
            val history = Intent(this, session_history_activity::class.java)
            finish()
            startActivity(history)
        }
        binding.home.setOnClickListener {
            deleteDecryptedFile()
            val goTohHomeFragment = Intent(this, MainActivity::class.java)
            finish()
            startActivity(goTohHomeFragment)
        }
        binding.DriveBottom.setOnClickListener {
            deleteDecryptedFile()
            val drive = Intent(this, drive_activity::class.java)
            finish()
            startActivity(drive)
        }
        binding.analyticsButton.setOnClickListener {
            deleteDecryptedFile()
            val analyticsPage = Intent(this, analysis_acitvity::class.java)
            finish()
            startActivity(analyticsPage)
        }
        binding.button4.setOnClickListener {
            deleteDecryptedFile()
            val toMessage = Intent(this, message_activity::class.java)
            finish()
            startActivity(toMessage)
        }
    }

    private fun decryptFile(encryptedFile: File): File {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        decryptedFile = File(filesDir, encryptedFile.name.replace("encrypted", "decrypted"))

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
    private fun deleteDecryptedFile() {
        if (::decryptedFile.isInitialized && decryptedFile.exists()) {
            decryptedFile.delete()
        }
    }
}




