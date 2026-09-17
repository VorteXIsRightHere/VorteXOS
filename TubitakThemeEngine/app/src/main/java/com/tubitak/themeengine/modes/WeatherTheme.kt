package com.tubitak.themeengine.modes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.net.URL

/**
 * Hava Durumu Teması: Konum ve hava durumuna göre arka plan resmi seçer.
 * Ayrıca konumun yerel saatini de hesaplar.
 */
class WeatherTheme(private val context: Context) {

    private val client = OkHttpClient()

    data class WeatherResult(
        val description: String,
        val tempCelsius: Double,
        val localTimeHour: Int,
        val wallpaperUrl: String
    )

    /**
     * OpenWeatherMap API ile hava durumu bilgisini çeker.
     * API key strings.xml'den okunur.
     */
    suspend fun fetchWeather(location: Location): WeatherResult? = withContext(Dispatchers.IO) {
        val apiKey = context.getString(R.string.openweather_api_key)
        if (apiKey.isBlank() || apiKey == "YOUR_OPENWEATHER_API_KEY") {
            return@withContext null
        }

        val url = "https://api.openweathermap.org/data/2.5/weather?" +
                "lat=${location.latitude}&lon=${location.longitude}&" +
                "appid=$apiKey&units=metric&lang=tr"

        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext null
            val json = JSONObject(body)

            val weatherArray = json.getJSONArray("weather")
            val weatherObj = weatherArray.getJSONObject(0)
            val description = weatherObj.getString("description")
            val main = weatherObj.getString("main") // Clear, Clouds, Rain, Snow, Thunderstorm, etc.

            val temp = json.getJSONObject("main").getDouble("temp")

            val timezoneOffset = json.getLong("timezone") // seconds from UTC
            val utcTime = System.currentTimeMillis() / 1000
            val localTimeSeconds = utcTime + timezoneOffset
            val localHour = ((localTimeSeconds / 3600) % 24).toInt()

            val wallpaperUrl = resolveWallpaperUrl(main, localHour)

            WeatherResult(description, temp, localHour, wallpaperUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Hava durumu ve saate göre uygun arka plan URL'sini döndürür.
     * Şimdilik Unsplash kaynaklı sabit URL'ler kullanılıyor.
     * Gerçek projede kendi CDN veya assetleriniz kullanılabilir.
     */
    private fun resolveWallpaperUrl(weatherMain: String, localHour: Int): String {
        val isNight = localHour < 6 || localHour > 20
        return when (weatherMain.lowercase()) {
            "clear" -> if (isNight) {
                "https://images.unsplash.com/photo-1532978379173-523e16f371f2?w=1080"
            } else {
                "https://images.unsplash.com/photo-1504386106334-03e5e5a0a0b0?w=1080"
            }
            "clouds" -> if (isNight) {
                "https://images.unsplash.com/photo-1504608524841-42fe6f032b4b?w=1080"
            } else {
                "https://images.unsplash.com/photo-1534088568595-a066f7bcd0d2?w=1080"
            }
            "rain", "drizzle" -> "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?w=1080"
            "snow" -> "https://images.unsplash.com/photo-1477601263568-180e2c22d35e?w=1080"
            "thunderstorm" -> "https://images.unsplash.com/photo-1501691223382-dfe7c13a52f0?w=1080"
            else -> "https://images.unsplash.com/photo-1465146344425-f00d5f5c8f07?w=1080"
        }
    }

    suspend fun downloadBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            URL(url).openStream().use { input ->
                BitmapFactory.decodeStream(input)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
