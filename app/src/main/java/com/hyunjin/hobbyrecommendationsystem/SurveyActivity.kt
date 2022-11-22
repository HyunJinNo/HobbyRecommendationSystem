package com.hyunjin.hobbyrecommendationsystem

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyunjin.hobbyrecommendationsystem.databinding.ActivitySurveyBinding
import com.opencsv.CSVReader
import java.io.InputStreamReader

class SurveyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySurveyBinding
    private lateinit var questions: Array<String>
    private lateinit var answers: FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val text: String = questions.get(position)
            holder.textView1.text = text
            holder.ratingBar.rating = 3.0f
            holder.ratingBar.setOnRatingBarChangeListener {ratingBar, rating, fromUser ->
                answers[position] = rating
            }
        }

        // 전체 데이터 개수 리턴
        override fun getItemCount() = questions.size
    }
}
