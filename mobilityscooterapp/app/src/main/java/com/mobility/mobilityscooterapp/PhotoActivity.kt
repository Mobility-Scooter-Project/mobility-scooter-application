package com.mobility.mobilityscooterapp

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import org.json.JSONObject


class PhotoActivity: AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var uploadButton: Button
    private lateinit var captureButton: Button
    private lateinit var confirmButton: Button
    private var photoUri: Uri? = null
    private var photoFile: File? = null

    private lateinit var verificationLayout: LinearLayout
    private lateinit var verificationTextView: TextView
    private lateinit var verificationIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_photo)


        imageView = findViewById(R.id.imageView)
        uploadButton = findViewById(R.id.upload_button)
        captureButton = findViewById(R.id.take_photo_button)
        confirmButton = findViewById(R.id.confirmButton)
        confirmButton.isEnabled = false

        verificationLayout = findViewById(R.id.verification_status_layout)
        verificationTextView = findViewById(R.id.body_verified_text)
        verificationIcon = findViewById(R.id.body_verified_icon)

        captureButton.setOnClickListener {
            takePhoto()
        }

        uploadButton.setOnClickListener {
            uploadPhoto()
        }

        confirmButton.setOnClickListener {
            photoUri?.let { sendToServer(it) }
        }
    }


    private fun takePhoto() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        val photoFile = File.createTempFile("photo_", ".jpg", cacheDir)
////        val photoFile = File(getExternalFilesDir(null), "photo.jpg")
//        photoUri = FileProvider.getUriForFile(this, "com.mobility.mobilityscooterapp.fileprovider", photoFile)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//        takePicture.launch(intent)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Store the photo in the app's private directory
        val imagesDir = getExternalFilesDir("images")
        photoFile = File(imagesDir, "photo_${System.currentTimeMillis()}.jpg")

        photoUri = FileProvider.getUriForFile(this, "com.mobility.mobilityscooterapp.fileprovider", photoFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        takePicture.launch(intent)
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            photoUri?.let {
                imageView.setImageURI(it)
                confirmButton.isEnabled = true
            }
        } else {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadPhoto() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        selectPicture.launch(intent)

        if (Build.VERSION.SDK_INT >= 34) { // Android 14+
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            selectPicture.launch(intent)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectPicture.launch(intent)
        }
    }

    private val selectPicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            if (result.data?.data != null) {
                val uri = result.data?.data
                photoUri = uri
                imageView.setImageURI(uri)
                confirmButton.isEnabled = true
            }
        } else {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendToServer(uri: Uri) {
        try {
//            val photoFile = File(uri.path.toString())
            val realPath = getRealPathFromURI(uri)
            if (realPath == null) {
                runOnUiThread {
                    Toast.makeText(this, "Error: Could not get image path", Toast.LENGTH_SHORT).show()
                }
                return
            }

            val photoFile = File(realPath)
            if (!photoFile.exists()) {
                runOnUiThread {
                    Toast.makeText(this, "Error: File does not exist: $realPath", Toast.LENGTH_LONG).show()
                }
                return
            }
            println("📸 Sending File: ${photoFile.absolutePath} (Exists: ${photoFile.exists()})")

            // create OkHttp client to make HTTP request
            val client = OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            // create multipart body to hold data/file we need to send to server
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", photoFile.name, photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull()))
                .build()
//                .addFormDataPart("photo", photoFile.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), photoFile))


            // create Request object to call the flask api using endpoint url
            val request: Request = Request.Builder()
                .url("https://mobilityscootermoblie.app/verify")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        println("Error: ${e.message}")
                        Toast.makeText(this@PhotoActivity, "Failed to connect to the server", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        val responseBody = response.body?.string()
                        println("server response: ${response.code} - ${response.message}")
                        println("response body: $responseBody")

                        if (responseBody != null) {
                            try {
                                val jsonResponse = JSONObject(responseBody)
                                val status = jsonResponse.getString("status") // get "status" field
                                val message = jsonResponse.getString("message") // get "message" field

                                if (status == "success") {
                                    Toast.makeText(
                                        this@PhotoActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    updateVerificationStatus(true)
                                } else {
                                    Toast.makeText(
                                        this@PhotoActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    updateVerificationStatus(false)
                                }
                            } catch (e: Exception) {
                                println("Error parsing JSON: ${e.message}")
                                Toast.makeText(
                                    this@PhotoActivity,
                                    "Error: Invalid response from server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }


//                        if (response.isSuccessful) {
//                            Toast.makeText(
//                                this@PhotoActivity,
//                                "Body was detected in the picture!",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            updateVerificationStatus(true);
//                        } else {
//                            Toast.makeText(
//                                this@PhotoActivity,
//                                "Body was not detected in the picture",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            updateVerificationStatus(false);
//                        }
                    }
                }
            })

        } catch (e: NullPointerException) {
            println("Error sending photo to Flask server")
        }
    } // end sendToServer

    private fun updateVerificationStatus(bodyVerified: Boolean) {
        if (bodyVerified) {
            verificationLayout.visibility = View.VISIBLE;
            verificationIcon.visibility = View.VISIBLE;
            verificationIcon.setImageResource(R.drawable.green_checkmark)
            verificationTextView.setText(R.string.body_verified)
            verificationTextView.setTextColor(ContextCompat.getColor(this, R.color.checkMarkGreen))
        } else {
            verificationLayout.visibility = View.VISIBLE;
            verificationIcon.visibility = View.VISIBLE;
            verificationIcon.setImageResource(R.drawable.x_mark)
            verificationTextView.setText(R.string.body_not_found)
            verificationTextView.setTextColor(ContextCompat.getColor(this, R.color.xMarkRed))
        }
    }

//    private fun getRealPathFromURI(contentUri: Uri): String? {
//        var result: String? = null
//        val cursor = contentResolver.query(contentUri, null, null, null, null)
//        if (cursor != null) {
//            cursor.moveToFirst()
//            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            result = cursor.getString(idx)
//            cursor.close()
//        }
//        return result
//    }

    private fun getRealPathFromURI(uri: Uri): String? {
        return when {
            uri.toString().startsWith("content://com.mobility.mobilityscooterapp.fileprovider") -> {
                // if the URI is from the camera (FileProvider), return the stored `photoFile`
                photoFile?.absolutePath
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                // if the image is from the Gallery, retrieve its actual file path
                var result: String? = null
                val cursor = contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                        if (columnIndex != -1) {
                            result = it.getString(columnIndex)
                        }
                    }
                }
                result
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                // if it's a direct file URI, return the path
                uri.path
            }
            else -> uri.path
        }
    }


//    private fun getRealPathFromURI(uri: Uri): String? {
//        // Handle Android 14+ (Use URI directly)
//        if (Build.VERSION.SDK_INT >= 34) {
//            return uri.toString()
//        }
//
//        // Check if the Uri is from the MediaStore (Gallery)
//        if ("content".equals(uri.scheme, ignoreCase = true)) {
//            var result: String? = null
//            val cursor = contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
//            cursor?.use {
//                if (it.moveToFirst()) {
//                    val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
//                    if (columnIndex != -1) {
//                        result = it.getString(columnIndex)
//                    }
//                }
//            }
//            return result
//        }
//
//        // If Uri is from the Camera, return the file path directly
//        return uri.path
//    }

}