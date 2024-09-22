package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private var inputText: String = DEFAULT_TEXT

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val DEFAULT_TEXT = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchBackButton = findViewById<Button>(R.id.search_back_button)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        searchBackButton.setOnClickListener {
            val searchBackButtonIntent = Intent(this, MainActivity::class.java)
            startActivity(searchBackButtonIntent)
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                clearButton.visibility = clearButtonVisibility(s)
                inputText = s.toString()
            }

        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(INPUT_TEXT, DEFAULT_TEXT)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else View.VISIBLE
    }
}