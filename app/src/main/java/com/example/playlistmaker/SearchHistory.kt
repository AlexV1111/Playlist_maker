package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val SEARCH_HISTORY_CONST = "SEARCH_HISTORY_CONST"

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private fun clearHistory() {
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY_CONST)
            .apply()
    }

    private fun saveTrack(tracksHistory: ArrayList<Track>) {
        val json = Gson().toJson(tracksHistory)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_CONST, json)
            .apply()
    }

    private fun readTrack(): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_CONST, null) ?: return ArrayList()
        return ArrayList(Gson().fromJson(json, Array<Track>::class.java).toList())
    }
}