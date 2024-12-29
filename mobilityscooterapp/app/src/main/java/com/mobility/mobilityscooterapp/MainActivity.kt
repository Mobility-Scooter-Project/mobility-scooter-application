package com.mobility.mobilityscooterapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    companion object {
        const val PREFS_NAME = "AppSettingsPrefs"
        const val DARK_MODE_KEY = "DarkMode"
    }

    private lateinit var navController: NavController

    // for sidebar from hamburger menu
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerMenu : ImageView

    // for new custom navbar
    private lateinit var homeButton: TextView
    private lateinit var driveButton: TextView
    private lateinit var analyticsButton: TextView
    private lateinit var messagesButton: TextView
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPref.getBoolean(DARK_MODE_KEY, false)
        setDarkMode(isDarkModeOn)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()

        val navHomeFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHomeFragment.navController

        // ------------- for new custom navbar ------------- //
        homeButton = findViewById(R.id.home)
        driveButton = findViewById(R.id.Drive_Bottom)
        analyticsButton = findViewById(R.id.analytics_button)
        messagesButton = findViewById(R.id.messages_button)

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
            navController.navigate(R.id.messages_page)
        }


        // ------------- for side menu ------------- //
        drawerLayout = findViewById(R.id.drawer_layout)
        hamburgerMenu = findViewById(R.id.hamburgerMenu)

        // opens/closes side menu
        hamburgerMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        // navigate to corresponding pages from side menu (NavigationView)
        val navigationView : NavigationView = findViewById(R.id.side_view)

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.side_view_home -> navController.navigate(R.id.homeFragment)
                R.id.side_view_drive -> navController.navigate(R.id.drive_start_page)
                R.id.side_view_analytics -> navController.navigate(R.id.analytic_start_page)
                R.id.side_view_messages -> navController.navigate(R.id.messages_page)
                R.id.side_view_logout -> {
                    firebaseAuth.signOut();

                    showToast(this,"Logout successful!");
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        /* updates coloring and selection of bottom navbar and
           side menu respectfully based on current view */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateNavBarColors(destination.id)
            updateSideMenuSelection(destination.id, navigationView)
        }

        //handleIntent()
    } // end onCreate

    /* function to update bottom navbar colors based on current view */
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
            R.id.messages_page -> {
                messagesButton.setBackgroundResource(R.drawable.active_messages_button)
            }
        }
    } // end updateNavBarColors

    /* function to update currently selected item in side menu based on current view */
    private fun updateSideMenuSelection(fragmentID: Int, navigationView: NavigationView) {
        val menu = navigationView.menu
        menu.findItem(R.id.side_view_home).isChecked = false
        menu.findItem(R.id.side_view_drive).isChecked = false
        menu.findItem(R.id.side_view_analytics).isChecked = false
        menu.findItem(R.id.side_view_messages).isChecked = false

        when (fragmentID) {
            R.id.homeFragment -> {
                menu.findItem(R.id.side_view_home).isChecked = true
            }
            R.id.drive_start_page -> {
                menu.findItem(R.id.side_view_drive).isChecked = true
            }
            R.id.analytic_start_page -> {
                menu.findItem(R.id.side_view_analytics).isChecked = true
            }
            R.id.messages_page -> {
                menu.findItem(R.id.side_view_messages).isChecked = true
            }
        }
    } // end updateSideMenuSelection

    private fun showToast(context: Context, message: String) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast_layout, null)

        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        toastText.text = message

        with (Toast(context)) {
            duration = Toast.LENGTH_LONG
            setGravity(Gravity.CENTER, 0, 0)
            view = layout
            show()
        }
    } // end showTest

    //Dark Mode setting

    private fun setDarkMode(isDarkMode: Boolean) {
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
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
    */
}


