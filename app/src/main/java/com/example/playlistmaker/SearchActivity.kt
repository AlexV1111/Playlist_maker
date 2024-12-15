package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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

const val TRACK_HISTORY_KEY = "track_history_key"
const val NEW_TRACK_KEY = "key_for_track_history"

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
    private lateinit var recyclerViewTrackHistory: RecyclerView

    private lateinit var placeholder: LinearLayout
    private lateinit var placeHolderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var updateBtn: Button

    private lateinit var searchHistoryTitle: TextView
    private lateinit var clearHistoryBtn: Button

    private val tracks = mutableListOf<Track>()
    private lateinit var trackAdapter: AdapterTrack

    private val trackHistory = mutableListOf<Track>()
    private lateinit var trackHistoryAdapter: HistoryAdapterTrack

//    val sharedPrefs = getSharedPreferences(TRACK_HISTORY_KEY, MODE_PRIVATE)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchBackButton = findViewById(R.id.search_back_button)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)

        recyclerViewTrack = findViewById(R.id.rv_track)
        recyclerViewTrackHistory = findViewById(R.id.rv_trackHistory)

        placeholder = findViewById(R.id.placeHolder)
        placeHolderImage = findViewById(R.id.placeHolderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        updateBtn = findViewById(R.id.updateBtn)

        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        clearHistoryBtn = findViewById(R.id.clearHistoryBtn)

        val onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(track: Track) {
                val position = tracks.indexOf(track)



                if (trackHistory.size > 9) {
                    trackHistory.removeAt(0)
                    trackAdapter.notifyItemRemoved(0)
                    trackAdapter.notifyItemRangeChanged(0, trackHistory.size)
                } else{

                }
                trackHistory.add(track)

                tracks.remove(track)
                trackAdapter.notifyItemRemoved(position)
                trackAdapter.notifyItemRangeChanged(position, tracks.size)

                trackHistoryAdapter.notifyDataSetChanged()
            }
        }


        trackAdapter = AdapterTrack(tracks, onItemClickListener)

        recyclerViewTrack.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTrack.setHasFixedSize(true)
        recyclerViewTrack.adapter = trackAdapter

        trackHistoryAdapter = HistoryAdapterTrack(trackHistory)

        recyclerViewTrackHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTrackHistory.setHasFixedSize(true)
        recyclerViewTrackHistory.adapter = trackHistoryAdapter


        searchBackButton.setOnClickListener {
            finish()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchHistoryTitle.visibility = View.GONE
                recyclerViewTrackHistory.visibility = View.GONE
                clearHistoryBtn.visibility = View.GONE
            }
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            inputEditText.clearFocus()
            placeholder.visibility = View.GONE
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        clearHistoryBtn.setOnClickListener {
            trackHistory.clear()
            trackHistoryAdapter.notifyDataSetChanged()
            searchHistoryTitle.visibility = View.GONE
            clearHistoryBtn.visibility = View.GONE
        }

        updateBtn.setOnClickListener {
            startService(inputEditText.text.toString())
        }

        inputEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    searchHistoryTitle.visibility = View.VISIBLE
                    recyclerViewTrackHistory.visibility = View.VISIBLE
                    clearHistoryBtn.visibility = View.VISIBLE
                }
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

    private fun startService(inputEditText: String) {
        iTunesService.searchTrack(inputEditText).enqueue(object :
            Callback<TrackResponse> {


            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {

                if (response.code() == 200) {

                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        trackAdapter.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()) showMessage(
                        getString(R.string.nothing_found),
                        true
                    )
                } else {
                    showMessage(
                        getString(R.string.nothing_found),
                        true
                    )
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showMessage(getString(R.string.something_went_wrong), false)
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showMessage(text: String, connectType: Boolean) {

        if (text.isNotEmpty()) {
            tracks.clear()
            trackAdapter.notifyDataSetChanged()

            placeholder.visibility = View.VISIBLE

            placeHolderImage.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
            updateBtn.visibility = View.VISIBLE


            placeholderMessage.text = text

            if (connectType) {
                placeHolderImage.setImageDrawable(getDrawable(R.drawable.nothing_found_image))
                updateBtn.visibility = View.GONE
            } else {
                placeHolderImage.setImageDrawable(getDrawable(R.drawable.connection_error))
            }

        } else placeholder.visibility = View.GONE

    }

}