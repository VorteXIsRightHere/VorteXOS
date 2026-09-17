package com.tubitak.themeengine.modes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.util.Calendar
import java.util.TimeZone

/**
 * Saate Uygun Tema: Cihaz saatine ve konumun yerel saatine göre
 * gündüz / gece / şafak / alacakaranlık temaları seçer.
 */
class TimeTheme(private val context: Context) {

    enum class DayPhase {
        DAWN, DAY, DUSK, NIGHT
    }

    data class TimeResult(
        val deviceHour: Int,
        val locationHour: Int,
        val phase: DayPhase,
        val wallpaperUrl: String
    )

    /**
     * Konumun yaklaşık yerel saatini hesaplar.
     * Daha kesin sonuç için TimeZoneDB veya Google Time Zone API kullanılabilir.
     */
    fun calculateLocationTime(location: Location): Calendar {
        val longitudeOffsetHours = (location.longitude / 15.0).toInt()
        val deviceTime = Calendar.getInstance()
        val locationTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        locationTime.timeInMillis = deviceTime.timeInMillis
        locationTime.add(Calendar.HOUR, longitudeOffsetHours - (deviceTime.get(Calendar.ZONE_OFFSET) / (1000 * 60 * 60)))
        return locationTime
    }

    fun resolvePhase(hour: Int): DayPhase {
        return when (hour) {
            in 5..7 -> DayPhase.DAWN
            in 8..17 -> DayPhase.DAY
            in 18..20 -> DayPhase.DUSK
            else -> DayPhase.NIGHT
        }
    }

    fun getThemeForLocation(location: Location): TimeResult {
        val deviceCalendar = Calendar.getInstance()
        val locationCalendar = calculateLocationTime(location)

        val deviceHour = deviceCalendar.get(Calendar.HOUR_OF_DAY)
        val locationHour = locationCalendar.get(Calendar.HOUR_OF_DAY)

        // Öncelik konum saatine verilir.
        val phase = resolvePhase(locationHour)
        val url = resolveWallpaperUrl(phase)

        return TimeResult(deviceHour, locationHour, phase, url)
    }

    private fun resolveWallpaperUrl(phase: DayPhase): String {
        return when (phase) {
            DayPhase.DAWN -> "https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?w=1080"
            DayPhase.DAY -> "https://images.unsplash.com/photo-1504386106334-03e5e5a0a0b0?w=1080"
            DayPhase.DUSK -> "https://images.unsplash.com/photo-1472120435266-53107fd0c44a?w=1080"
            DayPhase.NIGHT -> "https://images.unsplash.com/photo-1532978379173-523e16f371f2?w=1080"
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
