package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val TRACK_HISTORY_KEY = "track_history_key"
const val TRACK_HISTORY_SIZE = 10
const val SEARCH_DEBOUNCE_DELAY = 2000L

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

    private lateinit var inputEditText: EditText
    private lateinit var recyclerViewTrack: RecyclerView
    private lateinit var recyclerViewTrackHistory: RecyclerView

    private lateinit var placeholder: LinearLayout
    private lateinit var placeHolderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var updateBtn: Button

    private lateinit var searchHistoryTitle: TextView


    private lateinit var trackAdapter: AdapterTrack
    private lateinit var trackHistoryAdapter: AdapterTrack
    private val tracks = mutableListOf<Track>()
    private var trackHistory = mutableListOf<Track>()

    private lateinit var history: SearchHistory
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById((R.id.progressBar))

        inputEditText = findViewById(R.id.inputEditText)

        recyclerViewTrack = findViewById(R.id.rv_track)
        recyclerViewTrackHistory = findViewById(R.id.rv_trackHistory)

        placeholder = findViewById(R.id.placeHolder)
        placeHolderImage = findViewById(R.id.placeHolderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        updateBtn = findViewById(R.id.updateBtn)

        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)

        val sharedPrefs = getSharedPreferences(TRACK_HISTORY_KEY, MODE_PRIVATE)
        history = SearchHistory(sharedPrefs)

        if (sharedPrefs.getString(TRACK_HISTORY_KEY, null) != null) {
            trackHistory = history.readTrackHistory()
            searchHistoryTitle.visibility = View.VISIBLE
            recyclerViewTrackHistory.visibility = View.VISIBLE
            binding.clearHistoryBtn.visibility = View.VISIBLE
        } else {
            searchHistoryTitle.visibility = View.GONE
            recyclerViewTrackHistory.visibility = View.GONE
            binding.clearHistoryBtn.visibility = View.GONE
        }

        val onItemClickListener = OnItemClickListener { track ->
            history.addTrackToHistory(track, trackHistory)
            trackHistoryAdapter.notifyItemRangeChanged(0, trackHistory.size)
            trackHistoryAdapter.notifyDataSetChanged()

            val audioPlayerIntent = Intent(this@SearchActivity, AudioPlayer::class.java)
            audioPlayerIntent.putExtra(Track::class.simpleName, track)
            startActivity(audioPlayerIntent)
        }

        trackAdapter = AdapterTrack(tracks, onItemClickListener)

        recyclerViewTrack.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTrack.setHasFixedSize(true)
        recyclerViewTrack.adapter = trackAdapter

        trackHistoryAdapter = AdapterTrack(trackHistory, onItemClickListener)

        recyclerViewTrackHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTrackHistory.setHasFixedSize(true)
        recyclerViewTrackHistory.adapter = trackHistoryAdapter


        binding.searchBackButton.setOnClickListener {
            finish()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchHistoryTitle.visibility = View.GONE
                recyclerViewTrackHistory.visibility = View.GONE
                binding.clearHistoryBtn.visibility = View.GONE
            }
        }

        binding.clearIcon.setOnClickListener {
            inputEditText.setText("")
            inputEditText.clearFocus()
            placeholder.visibility = View.GONE
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        binding.clearHistoryBtn.setOnClickListener {
            history.clearTrackHistory(trackHistory)
            trackHistoryAdapter.notifyDataSetChanged()
            searchHistoryTitle.visibility = View.GONE
            binding.clearHistoryBtn.visibility = View.GONE
        }

        updateBtn.setOnClickListener {
            startService(inputEditText.text.toString())
        }

        inputEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                        searchHistoryTitle.visibility = View.VISIBLE
                        recyclerViewTrackHistory.visibility = View.VISIBLE
                        binding.clearHistoryBtn.visibility = View.VISIBLE
                    }

                    searchDebounce()
                }

                override fun afterTextChanged(s: Editable?) {
                    binding.clearIcon.visibility = clearButtonVisibility(s)
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

    override fun onStop() {
        super.onStop()
        if (trackHistory.isNotEmpty()) {
            history.saveTrackHistory(trackHistory)
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

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { startService(inputEditText.text.toString()) }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else View.VISIBLE
    }

    private fun startService(inputEditText: String) {

        if (inputEditText.isNotEmpty()){

            progressBar.visibility = View.VISIBLE
            placeholder.visibility = View.GONE

            iTunesService.searchTrack(inputEditText).enqueue(object :
                Callback<TrackResponse> {

                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    progressBar.visibility = View.GONE
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
                    progressBar.visibility = View.GONE
                    showMessage(getString(R.string.something_went_wrong), false)
                }
            })

        }


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