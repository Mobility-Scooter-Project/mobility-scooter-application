package com.mobility.mobilityscooterapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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


class PhotoActivity: AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var uploadButton: Button
    private lateinit var captureButton: Button
    private lateinit var confirmButton: Button
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_photo)


        imageView = findViewById(R.id.imageView)
        uploadButton = findViewById(R.id.upload_button)
        captureButton = findViewById(R.id.take_photo_button)
        confirmButton = findViewById(R.id.confirmButton)
        confirmButton.isEnabled = false

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

    }

    private fun uploadPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectPictureLauncher.launch(intent)
    }

    private val selectPictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
            val photoFile = File(uri.path.toString())

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
//                .url("http://127.0.0.1:5000/verify")
                .url("http://10.110.53.233:5000/verify")
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
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@PhotoActivity,
                                "Body was detected in the picture!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@PhotoActivity,
                                "Body was not detected in the picture",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })

        } catch (e: NullPointerException) {
            println("bruh")
        }


    }
}