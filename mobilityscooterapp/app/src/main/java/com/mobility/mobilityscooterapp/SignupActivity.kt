package com.mobility.mobilityscooterapp
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
import com.mobility.mobilityscooterapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

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

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){

                if (password.length > 8){
                if (password == confirmPassword){
                    if (termsConditionsChecked) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                            if(it.isSuccessful){
                                showToast(this, "You successfully registered!")
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        showToast(this, "please read and accept the terms and conditions")
                    }
                }else{
                    showToast(this, "Password doesn't matched")
                }
                }else{
                    showToast(this, "password at least 8 characters")
                }
            }else{
                showToast(this, "Fields cannot be empty")
            }
        }


        binding.backButton.setOnClickListener{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        binding.termsConditionsButton.setOnClickListener {
            val termsAndCondition = Intent(this,Terms_and_conditon_activity::class.java )
            startActivity(termsAndCondition)
        }

    }
}