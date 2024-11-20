package com.mobility.mobilityscooterapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.mobility.mobilityscooterapp.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class reset_password_activity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

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
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.resetButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            if (email.isNotEmpty()){
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
                        if (task.isSuccessful){
                            showToast(this,"Reset email send, Check your email")
                            finish()
                        }else{
                            showToast(this, "Invalid email address")
                        }
                    }
                }else{
                    showToast(this, "Invalid email address")
                }
            }else{
                showToast(this,"Fields cannot be empty!")
            }
        }
        binding.backButton.setOnClickListener {
            finish()
        }

//        binding.backText.setOnClickListener {
//            finish()
//        }

    }
}