package com.mobility.mobilityscooterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobility.mobilityscooterapp.databinding.ActivityAnalysisAcitvityBinding

class analysis_acitvity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalysisAcitvityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_acitvity)



        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, analytic_start_page())
                .commitNow()

        }
    }
}