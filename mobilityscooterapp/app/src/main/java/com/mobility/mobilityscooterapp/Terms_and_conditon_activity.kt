package com.mobility.mobilityscooterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobility.mobilityscooterapp.databinding.ActivityTermsAndConditonBinding

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