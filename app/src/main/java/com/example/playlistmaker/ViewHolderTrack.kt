package com.example.playlistmaker

import android.view.RoundedCorner
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ViewHolderTrack(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackImageView: ImageView = itemView.findViewById(R.id.trackImage)
    private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(data: Track) {

        Glide.with(itemView)
            .load(data.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(2))
            .into(trackImageView)

        trackNameView.text = data.trackName
        artistNameView.text = data.artistName
        trackTimeView.text = data.trackTime
    }

}