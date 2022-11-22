package com.hyunjin.hobbyrecommendationsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
}