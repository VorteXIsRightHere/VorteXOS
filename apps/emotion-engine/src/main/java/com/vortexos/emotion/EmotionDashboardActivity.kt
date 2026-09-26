package com.vortexos.emotion

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vortexos.emotion.databinding.ActivityEmotionDashboardBinding

class EmotionDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionDashboardBinding
    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var historyManager: EmotionHistoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPrefs = getSharedPreferences("emotion_prefs", Context.MODE_PRIVATE)
        if (!sharedPrefs.getBoolean("consent_given", false)) {
            startActivity(Intent(this, PrivacyConsentActivity::class.java))
            finish()
            return
        }

        binding = ActivityEmotionDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        historyManager = EmotionHistoryManager(this)

        if (checkPermission()) {
            setupUI()
        } else {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupUI()
        } else {
            binding.tvCurrentEmotion.text = getString(R.string.camera_permission_denied)
        }
    }

    private fun setupUI() {
        val sharedPrefs = getSharedPreferences("emotion_prefs", Context.MODE_PRIVATE)
        val isServiceRunning = sharedPrefs.getBoolean("service_running", false)
        binding.switchEnableService.isChecked = isServiceRunning

        binding.switchEnableService.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("service_running", isChecked).apply()
            if (isChecked) {
                val serviceIntent = Intent(this, EmotionDetectionService::class.java)
                ContextCompat.startForegroundService(this, serviceIntent)
            } else {
                stopService(Intent(this, EmotionDetectionService::class.java))
            }
        }
        
        updateHistoryUI()
    }
    
    private fun updateHistoryUI() {
        val history = historyManager.getRecentEmotions(10)
        val historyText = history.joinToString(separator = "\n") { "${it.timestamp}: ${it.emotion}" }
        binding.tvHistory.text = historyText.ifEmpty { getString(R.string.no_history) }
    }
}
