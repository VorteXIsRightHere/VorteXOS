package com.vortexos.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vortexos.settings.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    private var tapCount = 0

    private val themeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.versionText.text = "Version 1.0.0 (Codename Teknofest)"
        binding.kernelText.text = "Kernel x86_64"

        binding.versionText.setOnClickListener {
            tapCount++
            if (tapCount == 7) {
                Toast.makeText(this, "Easter Egg Activated: You are a developer!", Toast.LENGTH_SHORT).show()
                tapCount = 0
            }
        }

        val filter = IntentFilter().apply {
            addAction("com.vortexos.common.VorteXConstants.ACTION_WEATHER_CHANGED")
            addAction("com.vortexos.common.VorteXConstants.ACTION_EMOTION_CHANGED")
        }
        registerReceiver(themeReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(themeReceiver)
    }
}
