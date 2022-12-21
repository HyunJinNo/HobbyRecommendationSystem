package com.hyunjin.hobbyrecommendationsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hyunjin.hobbyrecommendationsystem.databinding.ActivityRecommendationBinding
import com.opencsv.CSVReader
import java.io.InputStreamReader
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

        // 테스트 코드
        for (x in cosineSimilarities) {
            println("ID: ${x.first}, Value: ${x.second}")
        }

        var count = 5
        val top5: Array<String> = mutableListOf<String>().apply {
            for (x in cosineSimilarities) {
                add(x.first)
                count--
                if (count <= 0) {
                    break
                }
            }
        }.toTypedArray()

        val questionsOfHobbies = arrayOf(
            "I'm interested in Internet",
            "I'm interested in Theatre",
            "I like Reading",
            "I'm interested in History",
            "I'm interested in Psychology",
            "I'm interested in Politics",
            "I'm interested in Mathematics",
            "I'm interested in Physics",
            "I'm interested in Biology",
            "I'm interested in Chemistry",
            "I'm interested in Geography",
            "I'm interested in Foreign languages",
            "I'm interested in Medicine",
            "I'm interested in Law",
            "I'm interested in Cars",
            "I'm interested in Art exhibitions",
            "I'm interested in Religion",
            "I like Dancing",
            "I like Playing musical instruments",
            "I like Writing",
            "I like Passive sport",
            "I like Active sport",
            "I like Gardening",
            "I like Shopping",
            "I'm interested in Science and technology",
            "I have Fun with friends (Socializing)",
            "I like Adrenaline sports",
            "I like Pets"
        )
        val hobbies = arrayOf(
            "Internet",
            "Watching Movies",
            "Reading",
            "History",
            "Psychology",
            "Politics",
            "Mathematics",
            "Physics",
            "Biology",
            "Chemistry",
            "Geography",
            "Studying Foreign languages",
            "Medicine",
            "Law",
            "Cars",
            "Visiting Art exhibitions",
            "Religion",
            "Dancing",
            "Playing musical instruments",
            "Writing",
            "Passive sport",
            "Active sport",
            "Gardening",
            "Shopping",
            "Studying Science and technology",
            "Socializing",
            "Adrenaline sports",
            "Pets"
        )
        val averages: Array<Triple<String, String, Double>> = mutableListOf<Triple<String, String, Double>>().apply {
            for (i in questionsOfHobbies.indices) {
                var sum = 0.0
                for (person in top5) {
                    sum += ratings[person]!![questionsOfHobbies[i]]!!.toString().toDouble()
                }
                add(Triple(questionsOfHobbies[i], hobbies[i], (sum / 5.0)))
            }
        }.sortedByDescending { it.third }.toTypedArray()

        runOnUiThread {
            binding.hobby1.apply {
                text = averages[0].second
                visibility = View.VISIBLE
            }
            binding.hobby2.apply {
                text = averages[1].second
                visibility = View.VISIBLE
            }
            binding.hobby3.apply {
                text = averages[2].second
                visibility = View.VISIBLE
            }
            binding.hobby4.apply {
                text = averages[3].second
                visibility = View.VISIBLE
            }
            binding.hobby5.apply {
                text = averages[4].second
                visibility = View.VISIBLE
            }
            binding.average1.apply {
                text = averages[0].third.toString()
                visibility = View.VISIBLE
            }
            binding.average2.apply {
                text = averages[1].third.toString()
                visibility = View.VISIBLE
            }

            binding.average3.apply {
                text = averages[2].third.toString()
                visibility = View.VISIBLE
            }

            binding.average4.apply {
                text = averages[3].third.toString()
                visibility = View.VISIBLE
            }

            binding.average5.apply {
                text = averages[4].third.toString()
                visibility = View.VISIBLE
            }

            var likeCount = 0
            binding.preference1.apply {
                val x = myRating[averages[0].first].toString().toInt()
                text = x.toString()
                visibility = View.VISIBLE
                if (x >= 4) {
                    likeCount++
                }
            }
            binding.preference2.apply {
                val x = myRating[averages[1].first].toString().toInt()
                text = x.toString()
                visibility = View.VISIBLE
                if (x >= 4) {
                    likeCount++
                }
            }
            binding.preference3.apply {
                val x = myRating[averages[2].first].toString().toInt()
                text = x.toString()
                visibility = View.VISIBLE
                if (x >= 4) {
                    likeCount++
                }
            }
            binding.preference4.apply {
                val x = myRating[averages[3].first].toString().toInt()
                text = x.toString()
                visibility = View.VISIBLE
                if (x >= 4) {
                    likeCount++
                }
            }
            binding.preference5.apply {
                val x = myRating[averages[4].first].toString().toInt()
                text = x.toString()
                visibility = View.VISIBLE
                if (x >= 4) {
                    likeCount++
                }
            }
            binding.precisionKTextView.apply {
                val precision = likeCount.toDouble() / 5.0
                text = "Precision@5: $precision"
                visibility = View.VISIBLE
            }
            binding.divider4.visibility = View.VISIBLE
            binding.divider5.visibility = View.VISIBLE
            binding.textView24.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.progressTextView.visibility = View.GONE
        }
    }
}