package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val searchButtonIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchButtonIntent)
            }
        }

        searchButton.setOnClickListener(searchButtonClickListener)

        val mediaButton = findViewById<Button>(R.id.media_button)
        mediaButton.setOnClickListener {
            val mediaButtonIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaButtonIntent)
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {
            val settingsButtonIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsButtonIntent)
        }
    }

}