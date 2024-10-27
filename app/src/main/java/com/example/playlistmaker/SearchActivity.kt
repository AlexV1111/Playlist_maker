package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var inputText: String = DEFAULT_TEXT

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val DEFAULT_TEXT = ""
    }

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var searchBackButton: Button
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerViewTrack: RecyclerView

    private lateinit var placeholder: LinearLayout
    private lateinit var placeHolderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var updateBtn: Button

    private val tracks = ArrayList<Track>()
    private val trackAdapter = AdapterTrack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchBackButton = findViewById(R.id.search_back_button)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)

        recyclerViewTrack = findViewById(R.id.rv_track)

        placeholder = findViewById(R.id.placeHolder)
        placeHolderImage = findViewById(R.id.placeHolderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        updateBtn = findViewById(R.id.updateBtn)


        trackAdapter.listTrack = tracks

        recyclerViewTrack.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTrack.setHasFixedSize(true)
        recyclerViewTrack.adapter = trackAdapter

        searchBackButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        updateBtn.setOnClickListener {
            startService(inputEditText.text.toString())
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

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    startService(inputEditText.text.toString())
                }
            }
            false
        }
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

    private fun startService(inputEditText: String){
        iTunesService.searchTrack(inputEditText).enqueue(object :
            Callback<TrackResponse> {

            var responseCode = 0

            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {

                responseCode = response.code()

                if (responseCode == 200) {
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        trackAdapter.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()) showMessage(
                        getString(R.string.nothing_found),
                        response.code()
                    )
                } else showMessage(
                    getString(R.string.something_went_wrong),
                    response.code()
                )
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showMessage(getString(R.string.something_went_wrong), responseCode)
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showMessage(text: String, responseCode: Int) {
        if (text.isNotEmpty()) {
            tracks.clear()
            trackAdapter.notifyDataSetChanged()

            placeHolderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            updateBtn.visibility = View.VISIBLE


            placeholderMessage.text = text

            if (responseCode == 200) {
                placeHolderImage.setImageDrawable(getDrawable(R.drawable.nothing_found_image))
                updateBtn.visibility = View.GONE
            } else placeHolderImage.setImageDrawable(getDrawable(R.drawable.connection_error))

        } else placeholder.visibility = View.GONE
    }

}