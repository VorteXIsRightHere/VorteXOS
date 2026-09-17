package com.tubitak.themeengine.modes

import android.content.Context
import android.graphics.Bitmap

/**
 * Duygu Durumu Teması: Kameradan kişinin yüz hatlarına göre duygu tespiti yapar.
 *
 * Desteklenen duygular:
 *  - MUTLU (Happy)
 *  - SİNİRLİ (Angry)
 *  - ÜZGÜN (Sad)
 *  - SAKİN (Calm)
 *
 * Her duygu için arka plan resmi ve sistem renkleri (accent, primary, secondary) tanımlanır.
 */
class EmotionTheme(private val context: Context) {

    enum class Emotion {
        HAPPY, ANGRY, SAD, CALM, UNKNOWN
    }

    data class EmotionResult(
        val emotion: Emotion,
        val displayName: String,
        val wallpaperUrl: String,
        val accentColor: Int,
        val primaryColor: Int,
        val secondaryColor: Int
    )

    fun getThemeForEmotion(emotion: Emotion): EmotionResult {
        return when (emotion) {
            Emotion.HAPPY -> EmotionResult(
                Emotion.HAPPY,
                "Mutlu",
                "https://images.unsplash.com/photo-1490750967868-88aa4486c0c8?w=1080",
                0xFFFFD700.toInt(), // Gold
                0xFFFFA500.toInt(), // Orange
                0xFFFFE4B5.toInt()  // Moccasin
            )
            Emotion.ANGRY -> EmotionResult(
                Emotion.ANGRY,
                "Sinirli",
                "https://images.unsplash.com/photo-1504608524841-42fe6f032b4b?w=1080",
                0xFFFF4500.toInt(), // OrangeRed
                0xFF8B0000.toInt(), // DarkRed
                0xFFFF6347.toInt()  // Tomato
            )
            Emotion.SAD -> EmotionResult(
                Emotion.SAD,
                "Üzgün",
                "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?w=1080",
                0xFF4682B4.toInt(), // SteelBlue
                0xFF2F4F4F.toInt(), // DarkSlateGray
                0xFFB0C4DE.toInt()  // LightSteelBlue
            )
            Emotion.CALM, Emotion.UNKNOWN -> EmotionResult(
                Emotion.CALM,
                "Sakin",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080",
                0xFF90EE90.toInt(), // LightGreen
                0xFF3CB371.toInt(), // MediumSeaGreen
                0xFFE0FFFF.toInt()  // LightCyan
            )
        }
    }

    suspend fun downloadBitmap(url: String): Bitmap? {
        return WeatherTheme(context).downloadBitmap(url)
    }
}
