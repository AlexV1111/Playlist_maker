package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class SearchHistory(
    private val sharedPrefs: SharedPreferences
) {

    fun addTrackToHistory(track: Track, trackHistory: MutableList<Track>) {
        val indexDouble = trackHistory.indexOfFirst { it.trackId == track.trackId }
        if (indexDouble >= 0) trackHistory.removeAt(indexDouble)
        if (trackHistory.size >= TRACK_HISTORY_SIZE) trackHistory.removeLast()
        trackHistory.add(0, track)
    }

    fun clearTrackHistory(trackHistory: MutableList<Track>) {
        trackHistory.clear()
        sharedPrefs.edit()
            .remove(TRACK_HISTORY_KEY)
            .apply()
    }

    fun saveTrackHistory(trackHistory: MutableList<Track>) {
        sharedPrefs.edit()
            .putString(TRACK_HISTORY_KEY, Gson().toJson(trackHistory))
            .apply()
    }

    fun readTrackHistory(): MutableList<Track> {
        return Gson().fromJson(
            sharedPrefs.getString(TRACK_HISTORY_KEY, null),
            Array<Track>::class.java
        ).toMutableList()
    }
}