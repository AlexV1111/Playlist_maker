package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

class SearchHistory(
    private val sharedPrefs: SharedPreferences
) {

    fun clearTrackHistory(trackHistory: MutableList<Track>) {
        trackHistory.clear()
        sharedPrefs.edit()
            .remove(TRACK_HISTORY_KEY)
            .apply()
    }

    fun saveTrackHistory(trackHistory: MutableList<Track>) {
        val json = Gson().toJson(trackHistory)
        sharedPrefs.edit()
            .putString(TRACK_HISTORY_KEY, json)
            .apply()
    }

    fun readTrackHistory(): MutableList<Track> {
        return Gson().fromJson(
            sharedPrefs.getString(TRACK_HISTORY_KEY, null),
            Array<Track>::class.java
        ).toMutableList()
    }
}