package com.hyunjin.hobbyrecommendationsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hyunjin.hobbyrecommendationsystem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.LogInButton.setOnClickListener {
            val id = binding.LogInIDEditText.text.toString()
            val password = binding.LogInPasswordEditText.text.toString()

            if (id.isEmpty()) {
                Toast.makeText(applicationContext, "Your ID is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(applicationContext, "Your password is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("Accounts").document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val data = document.data!!["password"]
                        if (password == data) {
                            Toast.makeText(applicationContext, "Succeeded in logging in", Toast.LENGTH_SHORT).show()
                            // TODO
                            // StartActivity(Intent(this, ?))
                        } else {
                            Toast.makeText(applicationContext, "Your password is not correct", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Your ID is not present", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed in logging in", Toast.LENGTH_SHORT).show()
                }
        }

        binding.SignUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}