package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private fun clearHistory() {
        sharedPrefs.edit()
            .remove(TRACK_HISTORY_KEY)
            .apply()
    }

    private fun saveTrack(trackHistory: MutableList<Track>) {
        val json = Gson().toJson(trackHistory)
        sharedPrefs.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }

    private fun readTrack(): MutableList<Track> {
        val json = sharedPrefs.getString(TRACK_HISTORY_KEY, null) ?: return mutableListOf()
        return ArrayList(Gson().fromJson(json, Array<Track>::class.java).toList())
    }
}