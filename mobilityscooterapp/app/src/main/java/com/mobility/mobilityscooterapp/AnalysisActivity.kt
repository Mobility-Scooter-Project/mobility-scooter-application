package com.mobility.mobilityscooterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobility.mobilityscooterapp.databinding.ActivityAnalysisActivityBinding

class AnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalysisActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        //Binding was not being used before needs to be tested
//        binding = ActivityAnalysisActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AnalyticStartPage())
                .commitNow()
        }
    }
}