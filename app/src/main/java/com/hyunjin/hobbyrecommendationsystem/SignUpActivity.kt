package com.hyunjin.hobbyrecommendationsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hyunjin.hobbyrecommendationsystem.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    
    // Access a Cloud Firestore instance from your Activity
    private val db = Firebase.firestore
    private var isAvailableID = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.CheckAvailabilityButon.setOnClickListener {
            val id = binding.SignUpIDEditText.text.toString()

            if (id.isEmpty()) {
                Toast.makeText(applicationContext, "Your ID is empty", Toast.LENGTH_SHORT).show()
            } else {
                db.collection("Accounts").document(id)
                    .get()
                    .addOnSuccessListener { document ->
                        isAvailableID = if (document.exists()) {
                            Toast.makeText(applicationContext, "Your ID is already present", Toast.LENGTH_SHORT).show()
                            binding.SignUpIDEditText.text.clear()
                            false
                        } else {
                            Toast.makeText(applicationContext, "Your ID is available", Toast.LENGTH_SHORT).show()
                            binding.SignUpIDEditText.isFocusable = false
                            true
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Failed in checking availability", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.SignUpButton2.setOnClickListener {
            val id = binding.SignUpIDEditText.text.toString()
            val password = binding.SignUpPasswordEditText.text.toString()
            val confirmation = binding.SignUpConfirmationEditText.text.toString()
            
            if (id.isEmpty()) {
                Toast.makeText(applicationContext, "Your ID is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!isAvailableID) {
                Toast.makeText(applicationContext, "Please check if your ID is available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(applicationContext, "Your password is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmation.isEmpty()) {
                Toast.makeText(applicationContext, "Please enter a password for confirmation", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password != confirmation) {
                Toast.makeText(applicationContext, "Failed to confirm your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new user with password
            val user = hashMapOf(
                "password" to password
            )

            // Add a new document
            db.collection("Accounts").document(id)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Succeeded in making a new account", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed in making a new account", Toast.LENGTH_SHORT).show()
                    return@addOnFailureListener
                }
        }
    }
}