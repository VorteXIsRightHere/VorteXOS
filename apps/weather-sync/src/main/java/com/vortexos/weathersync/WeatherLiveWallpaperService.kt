package com.vortexos.weathersync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.preference.PreferenceManager

class WeatherLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WeatherEngine()
    }

    inner class WeatherEngine : Engine(), SharedPreferences.OnSharedPreferenceChangeListener {
        private val handler = Handler(Looper.getMainLooper())
        private val particleSystem = WeatherParticleSystem()
        private var isVisible = false
        private var weatherType = WeatherType.UNKNOWN
        private lateinit var prefs: SharedPreferences
        
        private val weatherReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "com.vortexos.intent.action.WEATHER_CHANGED") {
                    val typeStr = intent.getStringExtra("weather_type")
                    weatherType = WeatherType.fromString(typeStr)
                    particleSystem.setWeatherType(weatherType)
                }
            }
        }

        private val drawRunner = Runnable { draw() }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            prefs = PreferenceManager.getDefaultSharedPreferences(this@WeatherLiveWallpaperService)
            prefs.registerOnSharedPreferenceChangeListener(this)
            
            val manual = prefs.getString("manual_weather", "AUTO")
            if (manual != "AUTO") {
                weatherType = WeatherType.fromString(manual)
                particleSystem.setWeatherType(weatherType)
            }
            
            val density = prefs.getInt("particle_density", 100)
            particleSystem.setDensity(density)

            val filter = IntentFilter("com.vortexos.intent.action.WEATHER_CHANGED")
            registerReceiver(weatherReceiver, filter)
            
            // Start background manager
            WeatherSyncManager.start(applicationContext)
        }

        override fun onDestroy() {
            super.onDestroy()
            prefs.unregisterOnSharedPreferenceChangeListener(this)
            unregisterReceiver(weatherReceiver)
            handler.removeCallbacks(drawRunner)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.isVisible = visible
            if (visible) {
                draw()
            } else {
                handler.removeCallbacks(drawRunner)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            particleSystem.setSize(width, height)
            draw()
        }
        
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key == "manual_weather") {
                val manual = prefs.getString("manual_weather", "AUTO")
                if (manual != "AUTO") {
                    weatherType = WeatherType.fromString(manual)
                    particleSystem.setWeatherType(weatherType)
                }
            } else if (key == "particle_density") {
                val density = prefs.getInt("particle_density", 100)
                particleSystem.setDensity(density)
            }
        }

        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    particleSystem.update()
                    particleSystem.draw(canvas)
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
            
            handler.removeCallbacks(drawRunner)
            if (isVisible) {
                handler.postDelayed(drawRunner, 1000L / 30L) // 30 FPS
            }
        }
    }
}
