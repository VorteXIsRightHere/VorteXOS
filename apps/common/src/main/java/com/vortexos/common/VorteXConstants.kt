package com.vortexos.common

/**
 * VorteXOS Sabitler
 * Tüm VorteXOS uygulamaları tarafından paylaşılan sabit değerler.
 */
object VorteXConstants {
    // OS Bilgileri
    const val OS_NAME = "VorteXOS"
    const val OS_VERSION = "1.0.0"
    const val OS_VERSION_CODE = 1
    const val OS_CODENAME = "Teknofest"

    // WeatherSync Broadcast Actions
    const val ACTION_WEATHER_CHANGED = "com.vortexos.weathersync.WEATHER_CHANGED"
    const val ACTION_THEME_CHANGED = "com.vortexos.weathersync.THEME_CHANGED"
    const val EXTRA_WEATHER_TYPE = "weather_type"
    const val EXTRA_ACCENT_COLOR = "accent_color"
    const val EXTRA_TEMPERATURE = "temperature"

    // Emotion Engine Broadcast Actions
    const val ACTION_EMOTION_CHANGED = "com.vortexos.emotion.EMOTION_CHANGED"
    const val EXTRA_EMOTION_TYPE = "emotion_type"
    const val EXTRA_EMOTION_CONFIDENCE = "emotion_confidence"

    // Hava Durumu Tipleri
    const val WEATHER_SUNNY = "sunny"
    const val WEATHER_RAINY = "rainy"
    const val WEATHER_SNOWY = "snowy"
    const val WEATHER_STORMY = "stormy"
    const val WEATHER_CLOUDY = "cloudy"
    const val WEATHER_FOGGY = "foggy"
    const val WEATHER_NIGHT = "night"
    const val WEATHER_SUNSET = "sunset"

    // Duygu Tipleri
    const val EMOTION_HAPPY = "happy"
    const val EMOTION_SAD = "sad"
    const val EMOTION_ANGRY = "angry"
    const val EMOTION_SURPRISED = "surprised"
    const val EMOTION_NEUTRAL = "neutral"
    const val EMOTION_TIRED = "tired"
    const val EMOTION_RELAXED = "relaxed"

    // SharedPreferences
    const val PREFS_NAME = "vortexos_prefs"
    const val PREF_CURRENT_WEATHER = "current_weather"
    const val PREF_CURRENT_EMOTION = "current_emotion"
    const val PREF_ACCENT_COLOR = "accent_color"
    const val PREF_WEATHER_ACCENT_COLOR = "weather_accent_color"
    const val PREF_EMOTION_ACCENT_COLOR = "emotion_accent_color"
    const val PREF_EMOTION_ENGINE_ENABLED = "emotion_engine_enabled"
    const val PREF_WEATHER_SYNC_ENABLED = "weather_sync_enabled"
}
