package com.vortexos.weathersync

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class WeatherApiClient {
    private val client = OkHttpClient()

    fun fetchWeather(lat: Double, lon: Double): WeatherType {
        val apiKey = BuildConfig.WEATHER_API_KEY
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${apiKey}"
        
        val request = Request.Builder().url(url).build()
        
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val json = JSONObject(responseBody)
                    val weatherArray = json.getJSONArray("weather")
                    if (weatherArray.length() > 0) {
                        val mainWeather = weatherArray.getJSONObject(0).getString("main")
                        return mapToWeatherType(mainWeather)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return WeatherType.UNKNOWN
    }
    
    private fun mapToWeatherType(apiWeather: String): WeatherType {
        return when (apiWeather.lowercase()) {
            "clear" -> WeatherType.SUNNY
            "rain", "drizzle" -> WeatherType.RAINY
            "snow" -> WeatherType.SNOWY
            "thunderstorm" -> WeatherType.STORMY
            "clouds" -> WeatherType.CLOUDY
            "mist", "smoke", "haze", "dust", "fog", "sand", "ash", "squall", "tornado" -> WeatherType.FOGGY
            else -> WeatherType.UNKNOWN
        }
    }
}
