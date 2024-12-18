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
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backToActivityMainButton = findViewById<Button>(R.id.back_to_activity_main_button)
        backToActivityMainButton.setOnClickListener {
            finish()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = (applicationContext as App).readSwitchTheme()
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
        (applicationContext as App).saveSwitchTheme()

        val shareAppButton = findViewById<Button>(R.id.share_app_button)
        shareAppButton.setOnClickListener {
            val shareAppButtonIntent = Intent(Intent.ACTION_SENDTO)
            shareAppButtonIntent.data = Uri.parse("mailto:")
            shareAppButtonIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.android_developer))
            startActivity(shareAppButtonIntent)
        }

        val writeToSupportButton = findViewById<Button>(R.id.write_to_support_button)
        writeToSupportButton.setOnClickListener {
            val writeToSupportButtonIntent = Intent(Intent.ACTION_SENDTO)
            writeToSupportButtonIntent.data = Uri.parse("mailto:")
            writeToSupportButtonIntent.putExtra(Intent.EXTRA_EMAIL,getString(R.string.my_mail_address))
            writeToSupportButtonIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.title_mail))
            writeToSupportButtonIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.body_of_mail))
            startActivity(writeToSupportButtonIntent)
        }

        val userAgreementButton = findViewById<Button>(R.id.user_agreement_button)
        userAgreementButton.setOnClickListener {
            val userAgreementButtonIntent = Intent(Intent.ACTION_VIEW)
            userAgreementButtonIntent.setData(Uri.parse(getString(R.string.link_user_agreement)))
            startActivity(userAgreementButtonIntent)
        }
    }
}