package com.vortexos.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Window
import com.google.android.material.color.MaterialColors

/**
 * VorteXOS Tema Yöneticisi
 * WeatherSync ve Emotion Engine'den gelen renk değişikliklerini yönetir.
 * Tüm VorteXOS uygulamaları bu sınıfı kullanarak dinamik tema güncellemeleri alır.
 */
class VorteXThemeManager(private val context: Context) {

    private var weatherAccentColor: Int = DEFAULT_ACCENT_COLOR
    private var emotionAccentColor: Int = DEFAULT_ACCENT_COLOR
    private var currentWeather: String = VorteXConstants.WEATHER_SUNNY
    private var currentEmotion: String = VorteXConstants.EMOTION_NEUTRAL
    private var onThemeChangedListener: OnThemeChangedListener? = null

    interface OnThemeChangedListener {
        fun onAccentColorChanged(color: Int)
        fun onWeatherChanged(weatherType: String, accentColor: Int)
        fun onEmotionChanged(emotionType: String, accentColor: Int)
    }

    private val themeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                VorteXConstants.ACTION_WEATHER_CHANGED -> {
                    currentWeather = intent.getStringExtra(VorteXConstants.EXTRA_WEATHER_TYPE)
                        ?: VorteXConstants.WEATHER_SUNNY
                    weatherAccentColor = intent.getIntExtra(
                        VorteXConstants.EXTRA_ACCENT_COLOR, DEFAULT_ACCENT_COLOR
                    )
                    saveToPrefs()
                    onThemeChangedListener?.onWeatherChanged(currentWeather, weatherAccentColor)
                    onThemeChangedListener?.onAccentColorChanged(getMergedAccentColor())
                }
                VorteXConstants.ACTION_EMOTION_CHANGED -> {
                    currentEmotion = intent.getStringExtra(VorteXConstants.EXTRA_EMOTION_TYPE)
                        ?: VorteXConstants.EMOTION_NEUTRAL
                    emotionAccentColor = getEmotionColor(currentEmotion)
                    saveToPrefs()
                    onThemeChangedListener?.onEmotionChanged(currentEmotion, emotionAccentColor)
                    onThemeChangedListener?.onAccentColorChanged(getMergedAccentColor())
                }
            }
        }
    }

    fun register(listener: OnThemeChangedListener) {
        onThemeChangedListener = listener
        loadFromPrefs()

        val filter = IntentFilter().apply {
            addAction(VorteXConstants.ACTION_WEATHER_CHANGED)
            addAction(VorteXConstants.ACTION_EMOTION_CHANGED)
            addAction(VorteXConstants.ACTION_THEME_CHANGED)
        }
        context.registerReceiver(themeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    fun unregister() {
        try {
            context.unregisterReceiver(themeReceiver)
        } catch (_: Exception) { }
        onThemeChangedListener = null
    }

    /** Hava durumu ve duygu renklerini birleştirerek ana accent color döndürür */
    fun getMergedAccentColor(): Int {
        return blendColors(weatherAccentColor, emotionAccentColor, 0.6f)
    }

    fun getCurrentWeather(): String = currentWeather
    fun getCurrentEmotion(): String = currentEmotion
    fun getWeatherAccentColor(): Int = weatherAccentColor
    fun getEmotionAccentColor(): Int = emotionAccentColor

    /** StatusBar rengini accent color'a göre günceller */
    fun applyToWindow(window: Window) {
        val color = getMergedAccentColor()
        window.statusBarColor = darkenColor(color, 0.3f)
        window.navigationBarColor = darkenColor(color, 0.4f)
    }

    private fun loadFromPrefs() {
        val prefs = context.getSharedPreferences(VorteXConstants.PREFS_NAME, Context.MODE_PRIVATE)
        currentWeather = prefs.getString(VorteXConstants.PREF_CURRENT_WEATHER, VorteXConstants.WEATHER_SUNNY)
            ?: VorteXConstants.WEATHER_SUNNY
        currentEmotion = prefs.getString(VorteXConstants.PREF_CURRENT_EMOTION, VorteXConstants.EMOTION_NEUTRAL)
            ?: VorteXConstants.EMOTION_NEUTRAL
        weatherAccentColor = prefs.getInt(VorteXConstants.PREF_WEATHER_ACCENT_COLOR, DEFAULT_ACCENT_COLOR)
        emotionAccentColor = prefs.getInt(VorteXConstants.PREF_EMOTION_ACCENT_COLOR, DEFAULT_ACCENT_COLOR)
    }

    private fun saveToPrefs() {
        context.getSharedPreferences(VorteXConstants.PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
            putString(VorteXConstants.PREF_CURRENT_WEATHER, currentWeather)
            putString(VorteXConstants.PREF_CURRENT_EMOTION, currentEmotion)
            putInt(VorteXConstants.PREF_WEATHER_ACCENT_COLOR, weatherAccentColor)
            putInt(VorteXConstants.PREF_EMOTION_ACCENT_COLOR, emotionAccentColor)
            apply()
        }
    }

    companion object {
        /** VorteXOS varsayılan accent color — Deep Purple */
        const val DEFAULT_ACCENT_COLOR = 0xFF7C4DFF.toInt()

        /** Duygu tipine göre renk döndürür */
        fun getEmotionColor(emotion: String): Int = when (emotion) {
            VorteXConstants.EMOTION_HAPPY -> Color.parseColor("#FFB300")      // Sıcak sarı
            VorteXConstants.EMOTION_SAD -> Color.parseColor("#5C6BC0")        // Yumuşak indigo
            VorteXConstants.EMOTION_ANGRY -> Color.parseColor("#00897B")      // Sakinleştirici teal
            VorteXConstants.EMOTION_SURPRISED -> Color.parseColor("#AB47BC")  // Mor
            VorteXConstants.EMOTION_NEUTRAL -> DEFAULT_ACCENT_COLOR           // Varsayılan
            VorteXConstants.EMOTION_TIRED -> Color.parseColor("#546E7A")      // Koyu gri-mavi
            VorteXConstants.EMOTION_RELAXED -> Color.parseColor("#66BB6A")    // Doğal yeşil
            else -> DEFAULT_ACCENT_COLOR
        }

        /** Hava durumuna göre renk döndürür */
        fun getWeatherColor(weather: String): Int = when (weather) {
            VorteXConstants.WEATHER_SUNNY -> Color.parseColor("#FFB300")      // Altın sarı
            VorteXConstants.WEATHER_RAINY -> Color.parseColor("#42A5F5")      // Mavi
            VorteXConstants.WEATHER_SNOWY -> Color.parseColor("#B3E5FC")      // Buz mavisi
            VorteXConstants.WEATHER_STORMY -> Color.parseColor("#4A148C")     // Koyu mor
            VorteXConstants.WEATHER_CLOUDY -> Color.parseColor("#78909C")     // Gri-mavi
            VorteXConstants.WEATHER_FOGGY -> Color.parseColor("#CFD8DC")      // Soluk gri
            VorteXConstants.WEATHER_SUNSET -> Color.parseColor("#FF7043")     // Turuncu-kırmızı
            VorteXConstants.WEATHER_NIGHT -> Color.parseColor("#1A237E")      // Lacivert
            else -> DEFAULT_ACCENT_COLOR
        }

        /** İki rengi belirli oranda karıştırır */
        fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
            val inverseRatio = 1f - ratio
            val r = (Color.red(color1) * ratio + Color.red(color2) * inverseRatio).toInt()
            val g = (Color.green(color1) * ratio + Color.green(color2) * inverseRatio).toInt()
            val b = (Color.blue(color1) * ratio + Color.blue(color2) * inverseRatio).toInt()
            return Color.rgb(r, g, b)
        }

        /** Rengi belirli oranda koyulaştırır */
        fun darkenColor(color: Int, factor: Float): Int {
            val a = Color.alpha(color)
            val r = (Color.red(color) * (1 - factor)).toInt().coerceIn(0, 255)
            val g = (Color.green(color) * (1 - factor)).toInt().coerceIn(0, 255)
            val b = (Color.blue(color) * (1 - factor)).toInt().coerceIn(0, 255)
            return Color.argb(a, r, g, b)
        }
    }
}
