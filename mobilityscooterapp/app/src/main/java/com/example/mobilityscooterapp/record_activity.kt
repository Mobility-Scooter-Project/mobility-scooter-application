package com.example.mobilityscooterapp

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.view.WindowManager
import android.widget.Chronometer
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.PermissionChecker
import androidx.documentfile.provider.DocumentFile
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.example.mobilityscooterapp.databinding.ActivityRecordPreviewBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


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

                            val sessionSummary = Intent(this, Driving_Session_Summary_activity::class.java).apply {
                                val videoUri = recordEvent.outputResults.outputUri

                                uploadVideoToFirebaseStorage(videoUri)

                                val encryptedFilePath = encryptFile(videoUri, contentResolver)

                                putExtra("date", date)
                                putExtra("start_time", startTimeFormatted)
                                putExtra("session_length", sessionLengthFormatted)
                                putExtra("encrypted_video_path", encryptedFilePath)
                                putExtra("video_timestamp", timeStamp)
                            }
                            finish()
                            startActivity(sessionSummary)


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




    private fun uploadVideoToFirebaseStorage(uri: Uri) {

        val storage = Firebase.storage
        val storageRef = storage.reference

        val videoRef = storageRef.child("videos/${uri.lastPathSegment}")

        val metadata = storageMetadata {
            contentType = "video/mp4"
        }

        // Upload the file to Firebase Storage
        videoRef.putFile(uri, metadata)
            .addOnSuccessListener {
                videoRef.downloadUrl.addOnSuccessListener { uri ->

                    videoUrl = uri.toString()
                    sendVideoToServer(videoUrl!!)

                    Toast.makeText(this, "Video uploaded successfully: $videoUrl", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to upload video: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendVideoToServer(url: String) {
        val client = OkHttpClient()


        val jsonObject = JSONObject()
        jsonObject.put("url", url)
        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body: RequestBody = jsonObject.toString().toRequestBody(jsonMediaType)

        // Build the request
        val request = Request.Builder()
            .url("https://your-app-url/analyze")
            .post(body)
            .build()

        // Send the request
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {

                Toast.makeText(this, "Failed to send video to server: ${response.message}", Toast.LENGTH_SHORT).show()
            } else {

                val responseData = response.body?.string()

                Toast.makeText(this, "Video processed successfully: $responseData", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /*
    private fun processFrames(uri: Uri) {

        var stableCount = 0
        var unstableCount = 0

        val tflite = Interpreter(loadModelFile(this.assets))
        val inputShape = tflite.getInputTensor(0).shape() // get the expected input shape

        // Get video frames
        val videoFrames = getVideoFrames(this, uri)

        // Run model on each frame
        for (frame in videoFrames) {
            val byteBuffer = convertBitmapToByteBuffer(frame, inputShape)
            val inputs: Array<Any> = arrayOf(byteBuffer)
            val outputs: MutableMap<Int, Any> = HashMap()
            outputs[0] = Array(1) { FloatArray(2) }

            tflite.runForMultipleInputsOutputs(inputs, outputs)

            val output = outputs[0] as Array<*>

            if (isStable(output)) {
                stableCount++
            } else {
                unstableCount++
            }
        }

        val totalFrames = stableCount + unstableCount
        val stablePercentage = stableCount * 100.0 / totalFrames
        val unstablePercentage = unstableCount * 100.0 / totalFrames

        // Display the results
        showResults(stablePercentage, unstablePercentage)
    }

    fun isStable(output: Array<*>) : Boolean {
        val prediction = output[0] as FloatArray
        return prediction[1] > 0.5
    }

    fun showResults(stablePercentage: Double, unstablePercentage: Double) {
        Toast.makeText(this, "Stable: $stablePercentage%, Unstable: $unstablePercentage%", Toast.LENGTH_SHORT).show()
    }

    fun getVideoFrames(context: Context, videoUri: Uri): List<Bitmap> {
        val mediaMetadataRetriever = MediaMetadataRetriever()

        mediaMetadataRetriever.setDataSource(context, videoUri)

        val videoLengthInMs =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong() ?: (0L * 1000)

        val frameIntervalMs = 1000000

        val videoFrames = mutableListOf<Bitmap>()

        var currentTimeMs = 0
        while (currentTimeMs < videoLengthInMs) {
            val bitmap = mediaMetadataRetriever.getFrameAtTime(currentTimeMs.toLong())
            if (bitmap != null) {
                videoFrames.add(bitmap)
            }
            currentTimeMs += frameIntervalMs
        }

        return videoFrames
    }


    private fun loadModelFile(assetManager: AssetManager): MappedByteBuffer {
        val modelPath = "model.tflite"
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap, inputShape: IntArray): ByteBuffer {
        val height: Int
        val width: Int
        val channels: Int

        if (inputShape.size == 4) {
            // Shape: [batch_size, height, width, channels]
            height = inputShape[1]
            width = inputShape[2]
            channels = inputShape[3]
        } else {
            // Shape: [height, width, channels]
            height = inputShape[0]
            width = inputShape[1]
            channels = inputShape[2]
        }

        // Create a ByteBuffer to hold the RGB data
        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(height * width * channels)

        // Fill the ByteBuffer with the RGB data from the bitmap
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixelValue: Int = bitmap.getPixel(x, y)

                byteBuffer.put((pixelValue shr 16 and 0xFF).toByte()) // Red channel
                byteBuffer.put((pixelValue shr 8 and 0xFF).toByte()) // Green channel
                byteBuffer.put((pixelValue and 0xFF).toByte()) // Blue channel
            }
        }

        // Reset the position of the buffer to the start
        byteBuffer.rewind()

        return byteBuffer
    }
     */

}