package com.hyunjin.hobbyrecommendationsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyunjin.hobbyrecommendationsystem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        id = intent.getStringExtra("ID")!!

        binding.logoutButton.setOnClickListener {
            finish()
        }

        binding.surveyButton.setOnClickListener {
            startActivity(Intent(this, SurveyActivity::class.java).apply {
                putExtra("ID", id)
            })
        }

        binding.recommendationButton.setOnClickListener {
            startActivity(Intent(this, RecommendationActivity::class.java).apply {
                putExtra("ID", id)
            })
        }
    }
}