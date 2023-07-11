package com.example.mobilityscooterapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mobilityscooterapp.databinding.ActivityLoginBinding
import com.example.mobilityscooterapp.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class reset_password_activity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.resetButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            if (email.isNotEmpty()){
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
                        if (task.isSuccessful){
                            Toast.makeText(this,"Reset email send, Check your email", Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{
                Toast.makeText(this,"Fields cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.backButton.setOnClickListener {
            finish()
        }

    }
}