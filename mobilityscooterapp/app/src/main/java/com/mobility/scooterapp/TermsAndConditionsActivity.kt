package com.mobility.scooterapp
/**
 * TermsAndConditionsActivity
 * Displays terms and conditions
 *
 */
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobility.scooterapp.databinding.ActivityTermsAndConditonBinding

class TermsAndConditionsActivity : AppCompatActivity() {

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