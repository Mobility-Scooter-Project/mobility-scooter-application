package com.example.mobilityscooterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobilityscooterapp.databinding.ActivitySignupBinding
import com.example.mobilityscooterapp.databinding.ActivityTermsAndConditonBinding
import com.google.firebase.auth.FirebaseAuth

class Terms_and_conditon_activity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsAndConditonBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
                finish()
        }
    }
}