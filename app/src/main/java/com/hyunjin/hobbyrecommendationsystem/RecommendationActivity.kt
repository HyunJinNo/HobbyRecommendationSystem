package com.hyunjin.hobbyrecommendationsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hyunjin.hobbyrecommendationsystem.databinding.ActivityRecommendationBinding
import com.opencsv.CSVReader
import java.io.InputStreamReader
import java.time.LocalTime
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.pow
import kotlin.math.sqrt

class RecommendationActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: ActivityRecommendationBinding
    private lateinit var id: String
    private lateinit var questions: Array<String>
    private lateinit var ratings: MutableMap<String, MutableMap<String, Any>>
    private lateinit var myRating: MutableMap<String, Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        id = intent.getStringExtra("ID")!!

        val assetManager = this.assets
        val inputStream = assetManager.open("responses.csv")
        val reader = CSVReader(InputStreamReader(inputStream))
        questions = reader.readNext()

        getAllRatings()
    }

    private fun getAllRatings() {
        ratings = mutableMapOf<String, MutableMap<String, Any>>().apply {
            db.collection("Accounts").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val rating = mutableMapOf<String, Any>().apply {
                            for (question in questions) {
                                put(question, document.data[question]!!)
                            }
                        }
                        this[document.id] = rating
                    }

                    myRating = ratings[id]!!
                    recommendHobby()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, "get failed with $exception", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    private fun recommendHobby() {
        var temp1 = 0.0
        for (question in questions) {
            temp1 += myRating[question].toString().toDouble().pow(2.0)
        }
        temp1 = sqrt(temp1)

        ratings.remove(id)

        // key: other user's id, value: value of cosine similarity
        val cosineSimilarities = mutableListOf<Pair<String, Double>>().apply {
            for (key in ratings.keys) {
                var numerator = 0.0
                var temp2 = 0.0
                for (question in questions) {
                    val value = ratings[key]!![question].toString().toDouble()
                    numerator += myRating[question].toString().toDouble() * value
                    temp2 += value.pow(2.0)
                }
                temp2 = sqrt(temp2)
                add(key to numerator / (temp1 * temp2))
            }
        }.sortedByDescending { it.second }

        for (x in cosineSimilarities) {
            println("ID: ${x.first}, Value: ${x.second}")
        }
    }
}