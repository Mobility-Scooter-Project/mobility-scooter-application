package com.mobility.mobilityscooterapp

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
                        showToast(this,"invalid email or password")
                    }
                }
            }else{
                showToast(this,"Fields cannot be empty!")
            }
        }
        binding.forgotPasswordButton.setOnClickListener {
            val resetPassword = Intent(this, reset_password_activity::class.java)
            startActivity(resetPassword)
        }
        binding.createAccountButton.setOnClickListener {
            val create_an_account_Intent = Intent(this, SignupActivity::class.java)
            startActivity(create_an_account_Intent)
        }
    }
    private fun setDarkMode(isDarkMode: Boolean) {
        Log.e("TESTING", "It works")
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}