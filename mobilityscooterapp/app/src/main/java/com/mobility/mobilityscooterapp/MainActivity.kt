package com.mobility.mobilityscooterapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHomeFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHomeFragment.navController

        bottomNav = findViewById(R.id.bottom_nav_view)

        bottomNav.setupWithNavController(navController)

        // Set a listener for navigation item selection
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    bottomNav.background = ContextCompat.getDrawable(this, R.color.bottom_home_color)
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.drive_start_page -> {
                    bottomNav.background = ContextCompat.getDrawable(this, R.color.bottom_drive_color)
                    navController.navigate(R.id.drive_start_page)
                    true
                }
                R.id.analytic_start_page -> {
                    bottomNav.background = ContextCompat.getDrawable(this, R.color.bottom_analytics_color)
                    navController.navigate(R.id.analytic_start_page)
                    true
                }
                R.id.messeges_page -> {
                    bottomNav.background = ContextCompat.getDrawable(this, R.color.bottom_messages_color)
                    navController.navigate(R.id.messeges_page)
                    true
                }
                else -> false
            }
        }

        //handleIntent()
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


