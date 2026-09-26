package com.vortexos.weathersync

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object WeatherSyncManager {
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private val apiClient = WeatherApiClient()
    
    private var isStarted = false

    fun start(context: Context) {
        if (isStarted) return
        isStarted = true
        
        executor.scheduleAtFixedRate({
            // Dummy location for now, would integrate play-services-location here normally
            val type = apiClient.fetchWeather(40.7128, -74.0060) // NY as default demo
            
            handler.post {
                broadcastWeatherChange(context, type)
            }
        }, 0, 30, TimeUnit.MINUTES)
    }
    
    private fun broadcastWeatherChange(context: Context, type: WeatherType) {
        val intent = Intent("com.vortexos.intent.action.WEATHER_CHANGED")
        intent.putExtra("weather_type", type.name)
        intent.putExtra("accent_color", type.accentColor)
        context.sendBroadcast(intent)
    }
}
