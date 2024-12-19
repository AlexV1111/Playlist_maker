package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import kotlin.properties.Delegates

private const val THEME_SWITCHER_KEY = "theme_switcher_key"
private const val NEW_SWITCHER_KEY = "new_switcher_key"

class App : Application() {

    private lateinit var sharedPrefs: SharedPreferences
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(THEME_SWITCHER_KEY, MODE_PRIVATE)
        if(sharedPrefs.contains(NEW_SWITCHER_KEY)){
            switchTheme(readSwitchTheme())
        }
    }

    fun isCurrentThemeDark(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    fun saveSwitchTheme() {
        sharedPrefs.edit()
            .putBoolean(NEW_SWITCHER_KEY, darkTheme)
            .apply()
    }

    fun readSwitchTheme(): Boolean {
        return sharedPrefs.getBoolean(NEW_SWITCHER_KEY, isCurrentThemeDark())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
