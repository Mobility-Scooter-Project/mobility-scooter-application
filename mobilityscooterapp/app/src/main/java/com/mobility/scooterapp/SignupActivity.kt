package com.mobility.scooterapp
/**
 * SignupActivity
 * Handles the process of user signing up their accounts for future access
 *
 * Responsibilities:
 * - Manages connection to server and generating the account to be saved
 * - Error handling for misinputs or user errors
 */
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.mobility.scooterapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
object SignupValidator {

    fun validateFields(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        termsChecked: Boolean
    ): SignupValidationResult {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return SignupValidationResult.Error("Fields cannot be empty")
        }

        if (password.length <= 8) {
            return SignupValidationResult.Error("Password must be at least 9 characters")
        }

        if (password != confirmPassword) {
            return SignupValidationResult.Error("Passwords do not match")
        }

        if (!termsChecked) {
            return SignupValidationResult.Error("Please read and accept the terms and conditions")
        }

        return SignupValidationResult.Success
    }

    sealed class SignupValidationResult {
        object Success : SignupValidationResult()
        data class Error(val message: String) : SignupValidationResult()
    }
}
class SignupActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignupBinding
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
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.createAccountButton.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val clinicCode = binding.clinicCode.text.toString()
            val termsConditionsChecked = binding.termsConditionsCheckbox.isChecked
            val result = SignupValidator.validateFields(firstName,lastName,email,password, confirmPassword,termsConditionsChecked)
            when(result) {
                is SignupValidator.SignupValidationResult.Success -> {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showToast(this, "You successfully registered!")
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                }
                is SignupValidator.SignupValidationResult.Error -> {
                    showToast(this, result.message)
                }
            }

        }


        binding.backButton.setOnClickListener{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        binding.termsConditionsButton.setOnClickListener {
            val termsAndCondition = Intent(this,TermsAndConditionsActivity::class.java )
            startActivity(termsAndCondition)
        }

    }
}