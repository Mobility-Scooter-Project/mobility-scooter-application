package com.mobility.mobilityscooterapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHomeFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHomeFragment.navController

        handleIntent()
    }

    private fun handleIntent() {
        val shouldNavigateToDrive = intent.getBooleanExtra("AutoNavigateToDrive", false)
        val shouldNavigateToAnalytics = intent.getBooleanExtra("AutoNavigateToAnalytics", false)
        val shouldNavigateToMessage = intent.getBooleanExtra("AutoNavigateToMessage", false)
        val shouldNavigateToHistory = intent.getBooleanExtra("AutoNavigateToHistory", false)
        if (shouldNavigateToDrive) {
            navController.navigate(R.id.action_homeFragment_to_drive_start_page)
        }
        if (shouldNavigateToAnalytics) {
            navController.navigate(R.id.action_homeFragment_to_analytic_start_page)
        }
        if (shouldNavigateToMessage) {
            navController.navigate(R.id.action_homeFragment_to_messages_page)
        }
        if (shouldNavigateToHistory) {
            val bundle = Bundle()
            bundle.putBoolean("fromHistory", true)
            Toast.makeText(this,"Text!", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.action_homeFragment_to_analytic_start_page, bundle)

        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }
}


