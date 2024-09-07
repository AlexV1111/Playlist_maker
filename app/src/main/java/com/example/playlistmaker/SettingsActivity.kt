package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backToActivityMainButton = findViewById<Button>(R.id.back_to_activity_main_button)
        backToActivityMainButton.setOnClickListener {
            val backToActivityMainButtonIntent = Intent(this, MainActivity::class.java)
            startActivity(backToActivityMainButtonIntent)
        }

    }
}