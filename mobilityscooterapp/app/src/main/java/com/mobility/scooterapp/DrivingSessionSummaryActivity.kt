package com.mobility.scooterapp
/**
 * DrivingSessionSummaryActivity
 * Handles the data from the Machine learning algorithm over the recording
 *
 * Responsibilities:
 * - Manages inputs from server for the video
 * - decrypts the files and deletes them afterwards
 * - Handles security logic for videos
 */
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.mobility.scooterapp.databinding.ActivityDrivingSessionSummaryBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import kotlinx.coroutines.*
import java.io.FileOutputStream

class DrivingSessionSummaryActivity : AppCompatActivity() {

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
        val estimateData = intent.getStringExtra("poseData")
        val dateTextView = findViewById<TextView>(R.id.textViewDate)
        val startTimeTextView = findViewById<TextView>(R.id.textViewStartTime)
        val sessionLengthTextView = findViewById<TextView>(R.id.SessionLength)
        val poseDataView = findViewById<TextView>(R.id.textViewEstimate)

        dateTextView.text = getString(R.string.date_placeholder, date)
        startTimeTextView.text = getString(R.string.start_time_placeholder, startTime)
        sessionLengthTextView.text = getString(R.string.session_length_placeholder, sessionLength)
        poseDataView.text = getString(R.string.estimate_estimate_placeholder, estimateData)

        // check if encrypted video file path exists
        if (encryptedFilePath != null) {
            val encryptedFile = File(encryptedFilePath)

            // database and firebase storage
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                Log.e(TAG, "User not authenticated")
                return
            }
            val storage = Firebase.storage
            val storageRef = storage.reference
            val db = Firebase.firestore
            val sessionDocRef = db.collection("users").document(userId!!).collection("sessions").document()

            if (encryptedFile.exists()) {
                val decryptedFile = decryptFile(encryptedFile)

                // this section runs in the background
                lifecycleScope.launch(Dispatchers.IO) {
                    // retrieve thumbnail
                    val bitmap: Bitmap? = try {
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(decryptedFile.path)
                        retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to retrieve thumbnail: ${e.message}")
                        null
                    }
                    // if thumbnail successfully retrieved
                    bitmap?.let {
                        // create a thumbnail from the decrypted video
                        val thumbnailFile =
                            File(externalCacheDir, "${encryptedFile.name}_thumbnail.png")

                        try {
                            FileOutputStream(thumbnailFile).use { fos ->
                                it.compress(Bitmap.CompressFormat.JPEG, 75, fos) // compress bitmap to JPEG
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error saving thumbnail to file: ${e.message}")
                            return@launch // exit if thumbnail saving fails
                        }

                        // if thumbnail exists and is not empty
                        if (thumbnailFile.exists() && thumbnailFile.length() > 0) {
                            val thumbnailRef =
                                storageRef.child("users/$userId/thumbnails/${thumbnailFile.name}")
                            val videoRef =
                                storageRef.child("users/$userId/videos/${encryptedFile.name}")

                            try {
                                // try to upload thumbnail and video to firebase storage, get download URLs
                                val thumbnailUri: Uri = Tasks.await(thumbnailRef.putFile(Uri.fromFile(thumbnailFile))
                                    .continueWithTask { thumbnailRef.downloadUrl }) // thumbnail

                                val videoUri: Uri = Tasks.await(videoRef.putFile(Uri.fromFile(decryptedFile))
                                    .continueWithTask { videoRef.downloadUrl }) // video

                                val sessionData = hashMapOf(
                                    "date" to date,
                                    "start_time" to startTime,
                                    "session_length" to sessionLength,
                                    "video_url" to videoUri.toString(),
                                    "thumbnail_url" to thumbnailUri.toString(),
                                    "dateTimeString" to "$date $startTime",
                                    "encryptedFilePath" to encryptedFilePath
                                )

                                // write session data to fire store
                                db.collection("users").document(userId!!).collection("sessions").document()
                                    .set(sessionData)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully written!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error writing document", e)
                                    }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error uploading video or thumbnail: ${e.message}")
                            }

                        } else {
                            Log.e(TAG, "Thumbnail file does not exist or is empty.")
                        }
                    } ?: run {
                        Log.e(TAG, "Thumbnail creation failed for video: ${decryptedFile.path}")
                    }
                }

                // start video_view_activity with path of decrypted video
                binding.videoView.setOnClickListener {
                    val watchVideo = Intent(this, VideoViewActivity::class.java).apply {
                        putExtra("video_path", decryptedFile.absolutePath)
                    }
                    startActivity(watchVideo)
                }

                // play video
                val videoUri = Uri.fromFile(decryptedFile)
                binding.videoView.setVideoURI(videoUri)

                // manage loop behavior
                val handler = Handler(Looper.getMainLooper())
                val runnable = object : Runnable {
                    override fun run() {
                        if (binding.videoView.currentPosition >= 5000) {
                            binding.videoView.seekTo(1)
                        }
                        handler.postDelayed(this, 100)
                    }
                }

                // video playback loop
                binding.videoView.setOnPreparedListener { mp ->
                    mp.isLooping = true
                    handler.postDelayed(runnable, 100)
                    binding.videoView.start()
                }
            }
        }

        //Navigation buttons
        binding.buttonNext.setOnClickListener {
            deleteDecryptedFile()
            val history = Intent(this, MainActivity::class.java)
            history.putExtra("AutoNavigateToHistory", true)
            history.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(history)
        }

        binding.home.setOnClickListener {
            deleteDecryptedFile()
            val goToHomeIntent = Intent(this, MainActivity::class.java)
            goToHomeIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(goToHomeIntent)
        }

        binding.DriveBottom.setOnClickListener {
            deleteDecryptedFile()
            val driveIntent = Intent(this, MainActivity::class.java)
            driveIntent.putExtra("AutoNavigateToDrive", true)
            driveIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(driveIntent)
        }

        binding.analyticsButton.setOnClickListener {
            deleteDecryptedFile()
            val analyticsPage = Intent(this, MainActivity::class.java)
            analyticsPage.putExtra("AutoNavigateToAnalytics", true)
            analyticsPage.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(analyticsPage)
        }

        binding.messagesButton.setOnClickListener {
            deleteDecryptedFile()
            val toMessage = Intent(this, MessageActivity::class.java)
            toMessage.putExtra("AutoNavigateToMessage", true)
            toMessage.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(toMessage)
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

    private fun deleteDecryptedFile() {
        if (::decryptedFile.isInitialized && decryptedFile.exists()) {
            decryptedFile.delete()
        }
    }
}