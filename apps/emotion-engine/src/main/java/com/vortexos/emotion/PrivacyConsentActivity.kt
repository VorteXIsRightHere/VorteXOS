package com.vortexos.emotion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vortexos.emotion.databinding.ActivityPrivacyConsentBinding

class PrivacyConsentActivity : AppCompatActivity() {

    private lateinit binding: ActivityPrivacyConsentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyConsentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("emotion_prefs", Context.MODE_PRIVATE)
        if (sharedPrefs.getBoolean("consent_given", false)) {
            startDashboard()
            return
        }

        binding.btnAgree.setOnClickListener {
            sharedPrefs.edit().putBoolean("consent_given", true).apply()
            startDashboard()
        }

        binding.btnDecline.setOnClickListener {
            Toast.makeText(this, R.string.consent_declined, Toast.LENGTH_LONG).show()
            finishAffinity()
        }
    }

    private fun startDashboard() {
        startActivity(Intent(this, EmotionDashboardActivity::class.java))
        finish()
    }
}
