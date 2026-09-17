package com.tubitak.themeengine.modes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

/**
 * Konuma Özel Tema: Cihazın anlık konumuna bağlı olarak harita görüntüsünü
 * duvar kağıdı olarak gösterir.
 *
 * Varsayılan olarak ücretsiz OpenStreetMap / Wikimedia Maps kullanılır.
 * Kullanıcı isterse Google Maps Static API key'i strings.xml'e ekleyerek
 * Google Maps görüntülerine de geçebilir.
 */
class LocationTheme(private val context: Context) {

    data class MapConfig(
        val width: Int = 1080,
        val height: Int = 1920,
        val zoom: Int = 15
    )

    /**
     * Konum için harita URL'si oluşturur.
     * Öncelik: Google Maps API key varsa Google Maps, yoksa Wikimedia Maps.
     */
    fun buildMapUrl(location: Location, config: MapConfig = MapConfig()): String {
        val googleKey = context.getString(R.string.google_maps_api_key)
        return if (googleKey.isNotBlank() && googleKey != "YOUR_GOOGLE_MAPS_API_KEY") {
            buildGoogleMapsUrl(location, config, googleKey)
        } else {
            buildWikimediaUrl(location, config)
        }
    }

    private fun buildGoogleMapsUrl(location: Location, config: MapConfig, apiKey: String): String {
        return "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=${location.latitude},${location.longitude}&" +
                "zoom=${config.zoom}&" +
                "size=${config.width}x${config.height}&" +
                "maptype=satellite&" +
                "key=$apiKey"
    }

    /**
     * Wikimedia Maps üzerinden ücretsiz OpenStreetMap görüntüsü oluşturur.
     * Kart/billing gerektirmez.
     */
    private fun buildWikimediaUrl(location: Location, config: MapConfig): String {
        // Wikimedia Maps: /img/{layer},{zoom},{lat},{lon},{width}x{height}.png
        return "https://maps.wikimedia.org/img/osm-intl," +
                "${config.zoom}," +
                "${location.latitude}," +
                "${location.longitude}," +
                "${config.width}x${config.height}.png?lang=tr"
    }

    suspend fun downloadMapImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
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
