package com.hyunjin.hobbyrecommendationsystem

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hyunjin.hobbyrecommendationsystem.databinding.ActivitySurveyBinding
import com.opencsv.CSVReader
import java.io.InputStreamReader

class SurveyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySurveyBinding
    private lateinit var id: String
    private lateinit var questions: Array<String>
    private lateinit var answers: FloatArray
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra("ID")!!

        val assetManager = this.assets
        val inputStream = assetManager.open("responses.csv")
        val reader = CSVReader(InputStreamReader(inputStream))
        questions = reader.readNext()
        answers = FloatArray(questions.size) { 3.0f }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정
        val adapter = MyAdapter(questions)
        recyclerView.adapter = adapter

        binding.submitButton.setOnClickListener {
            val data = mutableMapOf<String, Any>().apply {
                for (i in answers.indices) {
                    put(questions[i], answers[i].toInt())
                }
            }

            val ref = db.collection("Accounts").document(id)
            var password = ""

            ref.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        password = document.data!!["password"] as String
                        data.put("password", password)
                        ref.set(data)
                            .addOnSuccessListener {
                                Toast.makeText(applicationContext, "Succeeded in submitting the data", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(applicationContext, "(1) Failed in submitting the data", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                    } else {
                        Toast.makeText(applicationContext, "(2) Failed in submitting the data", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "(3) Failed in submitting the data", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    inner class MyAdapter(private val questions: Array<String>)
        : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        // 아이템 뷰를 저장하는 뷰홀더 클래스
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView1: TextView
            val ratingBar: RatingBar
            init {
                // 뷰 객체에 대한 참조
                textView1 = itemView.findViewById(R.id.text1)
                ratingBar = itemView.findViewById(R.id.ratingBar)
            }
        }

        // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view: View = inflater.inflate(R.layout.recyclerview_item, parent, false)
            val vh = ViewHolder(view)

            return vh
        }

        // position 에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val text: String = questions[position]
            holder.textView1.text = text
            holder.ratingBar.rating = answers[position]
            holder.ratingBar.setOnRatingBarChangeListener {ratingBar, rating, fromUser ->
                if (fromUser) {
                    answers[position] = rating
                }
            }
        }

        // 전체 데이터 개수 리턴
        override fun getItemCount() = questions.size
    }
}
