package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterTrack(
    private val listTrack: List<Track>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ViewHolderTrack>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTrack {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return ViewHolderTrack(view)
    }

    override fun getItemCount(): Int {
        return listTrack.size
    }

    override fun onBindViewHolder(holder: ViewHolderTrack, position: Int) {
        holder.bind(listTrack[position])
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(listTrack[holder.adapterPosition]) }
    }
}