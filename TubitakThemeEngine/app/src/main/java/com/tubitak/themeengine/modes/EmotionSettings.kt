package com.tubitak.themeengine.modes

import android.content.Context
import android.content.SharedPreferences

/**
 * Duygu durumu teması ayarlarını yönetir.
 *
 * - enabled: Duygu tespiti açık/kapalı
 * - sensitivity: Duygu tespiti hassasiyeti (0.0 - 1.0)
 * - detectionIntervalMs: İki tespit arası minimum süre
 */
class EmotionSettings(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var sensitivity: Float
        get() = prefs.getFloat(KEY_SENSITIVITY, 0.7f)
        set(value) = prefs.edit().putFloat(KEY_SENSITIVITY, value.coerceIn(0f, 1f)).apply()

    var detectionIntervalMs: Long
        get() = prefs.getLong(KEY_INTERVAL_MS, 3000L)
        set(value) = prefs.edit().putLong(KEY_INTERVAL_MS, value.coerceAtLeast(1000L)).apply()

    companion object {
        private const val PREFS_NAME = "tubitak_emotion_settings"
        private const val KEY_ENABLED = "emotion_enabled"
        private const val KEY_SENSITIVITY = "emotion_sensitivity"
        private const val KEY_INTERVAL_MS = "emotion_interval_ms"
    }
}
