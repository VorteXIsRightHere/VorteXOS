package com.vortexos.assistant

import android.content.Context
import android.content.Intent

class QuickActionHandler(private val context: Context) {
    fun handleCommand(text: String): String? {
        val lowerText = text.lowercase()
        if (lowerText.contains("havayı nasıl") || lowerText.contains("hava durumu")) {
            val intent = Intent().apply {
                action = "com.vortexos.weathersync.SHOW_WEATHER"
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Ignore
            }
            return "Hava durumu bilgisi açılıyor..."
        }
        
        if (lowerText.startsWith("uygulama aç")) {
            return "Uygulama açma özelliği yakında eklenecektir."
        }

        if (lowerText.startsWith("not al")) {
            return "Notunuz kaydedildi."
        }

        return null
    }
}
