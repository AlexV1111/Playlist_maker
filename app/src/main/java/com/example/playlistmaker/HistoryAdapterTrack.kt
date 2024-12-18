package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapterTrack(private val trackHistory: List<Track>) :
    RecyclerView.Adapter<ViewHolderTrack>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTrack {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return ViewHolderTrack(view)
    }

    override fun getItemCount(): Int {
        return trackHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolderTrack, position: Int) {
        holder.bind(trackHistory[position])
    }

}