package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToActivityMainButton.setOnClickListener { finish() }

        val appContext = applicationContext as App
        binding.themeSwitcher.isChecked = appContext.readSwitchTheme()
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            appContext.switchTheme(checked)
        }
        appContext.saveSwitchTheme()

        binding.shareAppButton.setOnClickListener {
            val shareAppButtonIntent = Intent(Intent.ACTION_SENDTO)
            shareAppButtonIntent.data = Uri.parse("mailto:")
            shareAppButtonIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer))
            startActivity(shareAppButtonIntent)
        }

        binding.writeToSupportButton.setOnClickListener {
            val writeToSupportButtonIntent = Intent(Intent.ACTION_SENDTO)
            writeToSupportButtonIntent.data = Uri.parse("mailto:")
            writeToSupportButtonIntent.putExtra(
                Intent.EXTRA_EMAIL,
                getString(R.string.my_mail_address)
            )
            writeToSupportButtonIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.title_mail)
            )
            writeToSupportButtonIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.body_of_mail))
            startActivity(writeToSupportButtonIntent)
        }

        binding.userAgreementButton.setOnClickListener {
            val userAgreementButtonIntent = Intent(Intent.ACTION_VIEW)
            userAgreementButtonIntent.setData(Uri.parse(getString(R.string.link_user_agreement)))
            startActivity(userAgreementButtonIntent)
        }
    }
}