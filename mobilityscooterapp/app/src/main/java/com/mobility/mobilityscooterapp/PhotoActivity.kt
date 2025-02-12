package com.mobility.mobilityscooterapp

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher

class PhotoActivity: AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var uploadButton: Button
    private lateinit var captureButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_photo)

        imageView = findViewById(R.id.imageView)
        uploadButton = findViewById(R.id.send_photo_layout)
        captureButton = findViewById(R.id.start_record_button)

        captureButton.setOnClickListener {
            takePhoto()
        }

        uploadButton.setOnClickListener {
            uploadPhoto()
        }
    }


    private fun takePhoto() {

    }

    private fun uploadPhoto() {
    }

}