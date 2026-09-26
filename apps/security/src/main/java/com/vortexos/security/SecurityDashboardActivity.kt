package com.vortexos.security

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vortexos.security.databinding.ActivitySecurityDashboardBinding

class SecurityDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecurityDashboardBinding

    private val themeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.vortexos.common.ACTION_WEATHER_CHANGED" ||
                intent.action == "com.vortexos.common.ACTION_EMOTION_CHANGED") {
                val accentColorStr = intent.getStringExtra("ACCENT_COLOR") ?: "#7C4DFF"
                try {
                    val color = Color.parseColor(accentColorStr)
                    binding.scoreProgress.setIndicatorColor(color)
                    // Apply to other views if necessary
                } catch (e: Exception) {
                    // Ignore parsing errors
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppLockManager.init(this)
        updateSecurityScore()

        binding.cardAppLock.setOnClickListener {
            startActivity(Intent(this, AppLockListActivity::class.java))
        }

        binding.cardPinSetup.setOnClickListener {
            startActivity(Intent(this, PinSetupActivity::class.java))
        }

        binding.cardPermissionInspector.setOnClickListener {
            startActivity(Intent(this, PermissionInspectorActivity::class.java))
        }

        binding.cardPrivacyMode.setOnClickListener {
            // Toggle privacy mode locally or via broadcast
            binding.switchPrivacyMode.isChecked = !binding.switchPrivacyMode.isChecked
        }

        val filter = IntentFilter().apply {
            addAction("com.vortexos.common.ACTION_WEATHER_CHANGED")
            addAction("com.vortexos.common.ACTION_EMOTION_CHANGED")
        }
        registerReceiver(themeReceiver, filter)
    }

    override fun onResume() {
        super.onResume()
        updateSecurityScore()
    }

    private fun updateSecurityScore() {
        val lockedCount = AppLockManager.getLockedApps().size
        val score = minOf(100, 50 + (lockedCount * 10))
        binding.scoreProgress.progress = score
        binding.textScore.text = score.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(themeReceiver)
    }
}
