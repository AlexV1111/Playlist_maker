package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayer : AppCompatActivity() {

    private lateinit var imageArtWorkView: ImageView
    private lateinit var trackNameView: TextView
    private lateinit var artistNameView: TextView

    private lateinit var trackTimeView: TextView
    private lateinit var collectionNameView: TextView
    private lateinit var releaseDateView: TextView
    private lateinit var primaryGenreNameView: TextView
    private lateinit var countryView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audioplayer)

        val audioPlayerBackButton = findViewById<ImageButton>(R.id.audioPlayerBackBtn)
        audioPlayerBackButton.setOnClickListener { finish() }

        val track = intent.getParcelableExtra<Track>(Track::class.simpleName)



        imageArtWorkView = findViewById(R.id.imageArtWork)
        trackNameView = findViewById(R.id.trackName)
        artistNameView = findViewById(R.id.artistName)
        trackTimeView = findViewById(R.id.trackTime)
        collectionNameView = findViewById(R.id.collectionName)
        releaseDateView = findViewById(R.id.releaseDate)
        primaryGenreNameView = findViewById(R.id.primaryGenreName)
        countryView = findViewById(R.id.country)


        if (track != null) {
            Glide.with(imageArtWorkView)
                .load(track.getCoverArtwork())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(track.dpToPx(8.0F, imageArtWorkView.context)))
                .into(imageArtWorkView)
        }

        trackNameView.text = track?.trackName
        artistNameView.text = track?.artistName
        trackTimeView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)
        collectionNameView.text = track?.collectionName
        releaseDateView.text = track?.getYearFromReleaseDate()
        primaryGenreNameView.text = track?.primaryGenreName
        countryView.text = track?.country

    }

}