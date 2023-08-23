package com.example.mobilityscooterapp

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.example.mobilityscooterapp.databinding.ActivityRecordPreviewBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class record_activity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityRecordPreviewBinding

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var date: String
    var startTime: Long = 0
    var startTimeFormatted: String = ""
    private var sessionLength: Long = 0
    private var timeStamp: String = ""

    private var videoUrl: String? = null
    private var dataFromServer: String? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        viewBinding = ActivityRecordPreviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        startCamera()


        viewBinding.recordButton.setOnClickListener {
            captureVideo()
        }
        viewBinding.backButton.setOnClickListener {
            finish()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun captureVideo() {
        val chronometer: Chronometer = findViewById(R.id.chronometer)
        val videoCapture = this.videoCapture ?: return

        viewBinding.recordButton.isEnabled = false


        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.P
            curRecording.stop()
            chronometer.stop()

            sessionLength = System.currentTimeMillis() - startTime

            recording = null
            return
        }
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()

        date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        startTime = System.currentTimeMillis()
        startTimeFormatted = SimpleDateFormat("hh:mm a", Locale.US).format(Date())



        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        viewBinding.recordButton.apply {
                            text = getString(R.string.stop_recording)
                            isEnabled = true
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {

                            sessionLength = System.currentTimeMillis() - startTime
                            val minutes = TimeUnit.MILLISECONDS.toMinutes(sessionLength)
                            val seconds = TimeUnit.MILLISECONDS.toSeconds(sessionLength) -
                                    TimeUnit.MINUTES.toSeconds(minutes)
                            val sessionLengthFormatted = String.format(Locale.US, "%02d min %02d sec", minutes, seconds)

                            val videoUri = recordEvent.outputResults.outputUri

                            uploadVideoToFirebaseStorage(videoUri) { downloadableUrl ->
                                sendVideoUrlToServer(downloadableUrl) { responseFromServer ->

                                    val encryptedFilePath = encryptFile(videoUri, contentResolver)

                                    val sessionSummary = Intent(this, Driving_Session_Summary_activity::class.java).apply {
                                        putExtra("date", date)
                                        putExtra("start_time", startTimeFormatted)
                                        putExtra("session_length", sessionLengthFormatted)
                                        putExtra("encrypted_video_path", encryptedFilePath)
                                        putExtra("video_timestamp", timeStamp)
                                        putExtra("poseData", responseFromServer)
                                    }
                                    finish()
                                    startActivity(sessionSummary)
                                }
                            }

                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        viewBinding.recordButton.apply {
                            text = getString(R.string.start_recording)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.LOWEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private fun encryptFile(fileUri: Uri, contentResolver: ContentResolver): String {

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val originalFile = DocumentFile.fromSingleUri(this, fileUri)
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val encryptedFile = File(filesDir, "encrypted_$timeStamp.mp4")

        val encryptedFileOutput = EncryptedFile.Builder(
            encryptedFile,
            this,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        contentResolver.openInputStream(fileUri)?.use { inputStream ->
            encryptedFileOutput.openFileOutput().use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }

        deleteFile(fileUri)

        return encryptedFile.absolutePath
    }

    private fun deleteFile(fileUri: Uri) {
        contentResolver.delete(fileUri, null, null)
    }

///////////////////////////////////////////////////////////////////////////////////


    private fun uploadVideoToFirebaseStorage(uri: Uri, onComplete: (String) -> Unit) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val videoRef = storageRef.child("videos/${uri.lastPathSegment}")
        val metadata = storageMetadata {
            contentType = "video/mp4"
        }

        // Upload the video
        videoRef.putFile(uri, metadata)
            .addOnSuccessListener {
                videoRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val downloadableUrl = "$downloadUrl?alt=media"
                    onComplete(downloadableUrl)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to upload video: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendVideoUrlToServer(videoUrl: String, onComplete: (String?) -> Unit) {
        val jsonBody = JSONObject().apply {
            put("url", videoUrl)
        }

        Log.d(TAG, "Sending video URL to server: $videoUrl")


        val mediaType = "application/json".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://mobilityscootermoblie.app/") // server url
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Network request failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseString = response.body?.string()
                    Log.d(TAG, "Successful response from server: $responseString")
                    onComplete(responseString)
                } else {
                    Log.e(TAG, "Error response from server: ${response.message}")
                }
            }
        })

    }

}