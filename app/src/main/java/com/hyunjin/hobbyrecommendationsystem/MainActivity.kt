package com.hyunjin.hobbyrecommendationsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hyunjin.hobbyrecommendationsystem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.LogInButton.setOnClickListener {
            // TODO
        }

        binding.SignUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload();
        }
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", it.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }

        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", it.exception)
                    Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }

        // [END sign-in_with_email]
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]

        val user = auth.currentUser!!
        user.sendEmailVerification().addOnCompleteListener(this) {
            // Email Verification sent
        }

        // [END sign_in_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
        // TODO
    }

    private fun reload() {
        // TODO
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}