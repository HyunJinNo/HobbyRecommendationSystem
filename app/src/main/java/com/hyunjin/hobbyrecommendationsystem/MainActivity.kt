package com.hyunjin.hobbyrecommendationsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hyunjin.hobbyrecommendationsystem.databinding.ActivityMainBinding
import com.opencsv.CSVReader
import kotlinx.coroutines.*
import java.io.InputStreamReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: ActivityMainBinding
    private lateinit var id: String
    private lateinit var questions: Array<String>
    private lateinit var ratings: MutableMap<String, MutableMap<String, Int>>
    private lateinit var myRating: MutableMap<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        id = intent.getStringExtra("ID")!!

        val assetManager = this.assets
        val inputStream = assetManager.open("responses.csv")
        val reader = CSVReader(InputStreamReader(inputStream))
        questions = reader.readNext()

        getAllRatings()
        recommendHobby(id)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // TODO: settingsFragment
            val intent = Intent(this, SurveyActivity::class.java)
            intent.putExtra("ID", id)
            startActivity(intent)
            true
        }
        R.id.action_logout -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun getAllRatings() {
        ratings = mutableMapOf<String, MutableMap<String, Int>>().apply {
            db.collection("Accounts").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val rating = mutableMapOf<String, Int>().apply {
                            for (question in questions) {
                                put(question, document.data[question].toString().toInt())
                            }
                        }
                        this[document.id] = rating
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, "get failed with $exception", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    private fun recommendHobby(id: String): String {
        var temp1 = 0.0
        myRating = mutableMapOf<String, Int>().apply {
            for (question in questions) {
                val num = ratings[id]!![question]!!
                put(question, num)
                temp1 += (num * num).toDouble()
            }
            temp1 = sqrt(temp1)
            remove(id)
        }

        val cosineSimilarities = mutableListOf<Pair<String, Double>>().apply {
            for (key in ratings.keys) {
                var numerator = 0.0
                var temp2 = 0.0
                for (question in questions) {
                    numerator += myRating[question]!! * ratings[key]!![question]!!
                    temp2 += ratings[key]!![question]!!.toDouble().pow(2.0)
                }
                temp2 = sqrt(temp2)
                add(key to numerator / (temp1 * temp2))
            }
        }.sortedByDescending { it.second }

        for (x in cosineSimilarities) {
            print("ID: ${x.first}, Value: ${x.second}")
        }

        // TODO
        return ""
    }
}