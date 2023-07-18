package com.example.mobilityscooterapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.example.mobilityscooterapp.databinding.ActivityDrivingSessionSummaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Driving_Session_Summary_activity : AppCompatActivity() {

    private lateinit var binding: ActivityDrivingSessionSummaryBinding


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
            /**/


            if (encryptedFile.exists()) {
                val decryptedFile = decryptFile(encryptedFile)

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
                val uploadTask = videoRef.putFile(Uri.fromFile(encryptedFile))
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "Error uploading video to Firebase Storage")
                }.addOnSuccessListener {
                    // Task successful, now we can store the session data in Firestore
                    val db = Firebase.firestore
                    val sessionData = hashMapOf(
                        "date" to date,
                        "start_time" to startTime,
                        "session_length" to sessionLength,
                        "video_url" to videoRef.path // or use download URL if you have it
                    )

                    db.collection("users").document(userId!!).collection("sessions").document()
                        .set(sessionData)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully written!")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error writing document", e)
                        }
                }
                /* end */




            } else {
                println("Error: encrypted file does not exist")
            }
        } else {
            println("Error: encrypted file path is null")
        }

        binding.buttonNext.setOnClickListener {
            val history = Intent(this, session_history_activity::class.java)
            finish()
            startActivity(history)
        }
        binding.home.setOnClickListener {
            val goTohHomeFragment = Intent(this, MainActivity::class.java)
            finish()
            startActivity(goTohHomeFragment)
        }
        binding.DriveBottom.setOnClickListener {
            val drive = Intent(this, drive_activity::class.java)
            finish()
            startActivity(drive)
        }
        binding.analyticsButton.setOnClickListener {
            val analyticsPage = Intent(this, analysis_acitvity::class.java)
            finish()
            startActivity(analyticsPage)
        }
        binding.button4.setOnClickListener {
            val toMessage = Intent(this, message_activity::class.java)
            finish()
            startActivity(toMessage)
        }


        /* This part is for the Database and firebase storage */



    }







    /* This part is for the Database and firebase storage */

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




