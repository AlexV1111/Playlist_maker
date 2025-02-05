package com.example.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.AudioplayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayer : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 500L
    }


    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    private lateinit var playBtn: ImageButton
    private lateinit var currentTrackTime: TextView
    private lateinit var mainThreadHandler: Handler
    private lateinit var timeRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainThreadHandler = Handler(Looper.getMainLooper())

        val binding = AudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.audioPlayerBackBtn.setOnClickListener { finish() }

        val track = intent.getParcelableExtra<Track>(Track::class.simpleName)

        if (track != null) {

            track.previewUrl?.let { preparePlayer(track.previewUrl) }
            timeRunnable = timer()



            Glide.with(binding.imageArtWork)
                .load(track.getCoverArtwork())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(track.dpToPx(8.0F, binding.imageArtWork.context)))
                .into(binding.imageArtWork)
        }

        binding.trackName.text = track?.trackName
        binding.artistName.text = track?.artistName
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)
        binding.collectionName.text = track?.collectionName
        binding.releaseDate.text = track?.getYearFromReleaseDate()
        binding.primaryGenreName.text = track?.primaryGenreName
        binding.country.text = track?.country

        playBtn = findViewById(R.id.playBtn)
        playBtn.setOnClickListener { playbackControl() }

        currentTrackTime = findViewById(R.id.currentTrackTime)

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacks(timeRunnable)
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playBtn.setImageResource(R.drawable.play_btn)
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playBtn.setImageResource(R.drawable.play_btn)
            mainThreadHandler.removeCallbacks(timeRunnable)
            playerState = STATE_PREPARED
            currentTrackTime.text = getString(R.string.start_time)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playBtn.setImageResource(R.drawable.pause_btn)
        playerState = STATE_PLAYING
        mainThreadHandler.postDelayed(timeRunnable, DELAY)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playBtn.setImageResource(R.drawable.play_btn)
        playerState = STATE_PAUSED
        mainThreadHandler.removeCallbacks(timeRunnable)
    }

    private fun timer(): Runnable {
        return object : Runnable {
            override fun run() {
                currentTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                mainThreadHandler.postDelayed(this, DELAY)
            }
        }
    }

}