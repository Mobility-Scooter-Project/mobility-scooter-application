package com.mobility.mobilityscooterapp
/**
 * DriveActivity
 * Handles users login access
 *
 * Responsibilities:
 * - Manage connection between user database and user inputs
 * - Shows toasts based off the inputs correctness
 */
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.mobility.mobilityscooterapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.mobility.mobilityscooterapp.MainActivity.Companion

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    companion object {
        const val PREFS_NAME = "AppSettingsPrefs"
        const val DARK_MODE_KEY = "DarkMode"
    }

    /**
     * Called when user attempts login
     *
     * This method checks whether the login credientals are valid
     * WIll show toast based on the result
     *
     * @param context the current context that is default for the app to get current data
     * @param message what will be displayed on the toast
     */
    fun showToast(context: Context, message: String) {
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = getSharedPreferences(com.mobility.mobilityscooterapp.MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPref.getBoolean(MainActivity.DARK_MODE_KEY, false)
        setDarkMode(isDarkModeOn)
        firebaseAuth = FirebaseAuth.getInstance()
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED)){
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1001)
        }
        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        showToast(this,"Login successful!")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        showToast(this,"Invalid email or password")
                    }
                }
            }else{
                showToast(this,"Fields cannot be empty!")
            }
        }
        binding.forgotPasswordButton.setOnClickListener {
            val resetPassword = Intent(this, ResetPasswordActivity::class.java)
            startActivity(resetPassword)
        }
        binding.createAccountButton.setOnClickListener {
            val create_an_account_Intent = Intent(this, SignupActivity::class.java)
            startActivity(create_an_account_Intent)
        }
    }
    /**
     * Called everytime the app is running and will be used based off app's preferences
     *
     * This method checks whether the dark mode feature is enabled
     *
     * @param isDarkMode checks the dark mode feature is activated or not within the app
     */
    private fun setDarkMode(isDarkMode: Boolean) {
        Log.e("TESTING", "It works")
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}