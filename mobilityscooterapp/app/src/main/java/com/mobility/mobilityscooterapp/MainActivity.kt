package com.mobility.mobilityscooterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    // for new custom navbar
    private lateinit var homeButton: TextView
    private lateinit var driveButton: TextView
    private lateinit var analyticsButton: TextView
    private lateinit var messagesButton: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHomeFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHomeFragment.navController

        // for new custom navbar
        homeButton = findViewById(R.id.home)
        driveButton = findViewById(R.id.Drive_Bottom)
        analyticsButton = findViewById(R.id.analytics_button)
        messagesButton = findViewById(R.id.button4)

        homeButton.setOnClickListener {
            navController.navigate(R.id.homeFragment)
        }

        driveButton.setOnClickListener {
            navController.navigate(R.id.drive_start_page)
        }

        analyticsButton.setOnClickListener {
            navController.navigate(R.id.analytic_start_page)
        }

        messagesButton.setOnClickListener {
            navController.navigate(R.id.messeges_page)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateNavBarColors(destination.id)
        }

        //handleIntent()
    }

    private fun updateNavBarColors(fragmentID: Int) {

        homeButton.setBackgroundResource(R.drawable.home_button_bg)
        driveButton.setBackgroundResource(R.drawable.drive_bottom_bg)
        analyticsButton.setBackgroundResource(R.drawable.analytics_button_bg)
        messagesButton.setBackgroundResource(R.drawable.messages_button_bg)

        when (fragmentID) {
            R.id.homeFragment -> {
                homeButton.setBackgroundResource(R.drawable.active_home_button)
            }
            R.id.drive_start_page -> {
                driveButton.setBackgroundResource(R.drawable.active_drive_button)
            }
            R.id.analytic_start_page -> {
                analyticsButton.setBackgroundResource(R.drawable.active_analytics_button)
            }
            R.id.messeges_page -> {
                messagesButton.setBackgroundResource(R.drawable.active_messages_button)
            }
        }
    }


    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
    */


    /*
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
            navController.navigate(R.id.action_homeFragment_to_messeges_page)
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
    */
}


