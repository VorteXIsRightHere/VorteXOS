import os

base_dir = r"C:\Users\VorteX\.gemini\antigravity\scratch\VorteXOS\apps\weather-sync"

files = {
    "build.gradle.kts": """plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.vortexos.weathersync"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.vortexos.weathersync"
        minSdk = 28
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        
        buildConfigField("String", "WEATHER_API_KEY", "\\"YOUR_OPENWEATHERMAP_API_KEY\\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // implementation(project(":apps:common")) // Assuming the common module exists
}
""",
    "src/main/AndroidManifest.xml": """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.vortexos.weathersync">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.SET_WALLPAPER" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.WeatherSync">

        <activity
            android:name=".WeatherSettingsActivity"
            android:exported="true"
            android:label="@string/title_activity_settings">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".WeatherLiveWallpaperService"
            android:label="@string/app_name"
            android:permission="android.permission.BIND_WALLPAPER"
            android:exported="true">
            <intent-filter>
                <action android:name="android.service.wallpaper.WallpaperService" />
            </intent-filter>
            <meta-data
                android:name="android.service.wallpaper"
                android:resource="@xml/wallpaper" />
        </service>

    </application>

</manifest>
""",
    "src/main/res/xml/wallpaper.xml": """<?xml version="1.0" encoding="utf-8"?>
<wallpaper xmlns:android="http://schemas.android.com/apk/res/android"
    android:settingsActivity="com.vortexos.weathersync.WeatherSettingsActivity"
    android:thumbnail="@mipmap/ic_launcher"
    android:description="@string/wallpaper_description"
    android:author="VorteXOS" />
""",
    "src/main/res/xml/preferences.xml": """<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:app="http://schemas.android.com/apk/res-auto">
    <SwitchPreferenceCompat
        app:key="enable_weathersync"
        app:title="@string/pref_enable_weathersync"
        app:defaultValue="true" />
        
    <ListPreference
        app:key="manual_weather"
        app:title="@string/pref_manual_weather"
        app:entries="@array/weather_types"
        app:entryValues="@array/weather_type_values"
        app:defaultValue="AUTO"
        app:useSimpleSummaryProvider="true" />
        
    <SeekBarPreference
        app:key="particle_density"
        app:title="@string/pref_particle_density"
        app:min="10"
        android:max="200"
        app:defaultValue="100" />
</PreferenceScreen>
""",
    "src/main/res/values/strings.xml": """<resources>
    <string name="app_name">WeatherSync</string>
    <string name="title_activity_settings">WeatherSync Settings</string>
    <string name="wallpaper_description">Live wallpaper reflecting current weather.</string>
    <string name="pref_enable_weathersync">Enable WeatherSync</string>
    <string name="pref_manual_weather">Manual Weather (Demo)</string>
    <string name="pref_particle_density">Particle Density</string>
    
    <string-array name="weather_types">
        <item>Auto (GPS)</item>
        <item>Sunny</item>
        <item>Rainy</item>
        <item>Snowy</item>
        <item>Stormy</item>
        <item>Cloudy</item>
        <item>Foggy</item>
        <item>Sunset</item>
        <item>Night</item>
    </string-array>
    
    <string-array name="weather_type_values">
        <item>AUTO</item>
        <item>SUNNY</item>
        <item>RAINY</item>
        <item>SNOWY</item>
        <item>STORMY</item>
        <item>CLOUDY</item>
        <item>FOGGY</item>
        <item>SUNSET</item>
        <item>NIGHT</item>
    </string-array>
</resources>
""",
    "src/main/res/values-tr/strings.xml": """<resources>
    <string name="app_name">HavaSenkronize</string>
    <string name="title_activity_settings">HavaSenkronize Ayarları</string>
    <string name="wallpaper_description">Mevcut hava durumunu yansıtan canlı duvar kağıdı.</string>
    <string name="pref_enable_weathersync">HavaSenkronize Etkinleştir</string>
    <string name="pref_manual_weather">Manuel Hava Durumu (Demo)</string>
    <string name="pref_particle_density">Parçacık Yoğunluğu</string>
    
    <string-array name="weather_types">
        <item>Otomatik (GPS)</item>
        <item>Güneşli</item>
        <item>Yağmurlu</item>
        <item>Karlı</item>
        <item>Fırtınalı</item>
        <item>Bulutlu</item>
        <item>Sisli</item>
        <item>Gün Batımı</item>
        <item>Gece</item>
    </string-array>
    
    <string-array name="weather_type_values">
        <item>AUTO</item>
        <item>SUNNY</item>
        <item>RAINY</item>
        <item>SNOWY</item>
        <item>STORMY</item>
        <item>CLOUDY</item>
        <item>FOGGY</item>
        <item>SUNSET</item>
        <item>NIGHT</item>
    </string-array>
</resources>
""",
    "src/main/res/values/colors.xml": """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="vortex_purple">#7C4DFF</color>
    <color name="vortex_dark">#121212</color>
</resources>
""",
    "src/main/res/values/themes.xml": """<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.WeatherSync" parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">@color/vortex_purple</item>
        <item name="android:windowBackground">@color/vortex_dark</item>
    </style>
</resources>
""",
    "src/main/layout/activity_settings.xml": """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="?android:attr/colorBackground">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            app:title="@string/title_activity_settings"
            xmlns:app="http://schemas.android.com/apk/res-auto" />
    </com.google.android.material.appbar.AppBarLayout>

    <FrameLayout
        android:id="@+id/settings_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
""",
    "src/main/java/com/vortexos/weathersync/WeatherType.kt": """package com.vortexos.weathersync

enum class WeatherType(val accentColor: String) {
    SUNNY("#FFB300"),
    RAINY("#1E88E5"),
    SNOWY("#FFFFFF"),
    STORMY("#5E35B1"),
    CLOUDY("#90A4AE"),
    FOGGY("#B0BEC5"),
    SUNSET("#FF7043"),
    NIGHT("#3949AB"),
    UNKNOWN("#7C4DFF");
    
    companion object {
        fun fromString(value: String?): WeatherType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
""",
    "src/main/java/com/vortexos/weathersync/WeatherSettingsActivity.kt": """package com.vortexos.weathersync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.vortexos.weathersync.databinding.ActivitySettingsBinding

class WeatherSettingsActivity : AppCompatActivity() {

    private lateinit binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }
}
""",
    "src/main/java/com/vortexos/weathersync/WeatherLiveWallpaperService.kt": """package com.vortexos.weathersync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
            isVisible = visible
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
                handler.postDelayed(drawRunner, 1000 / 30) // 30 FPS
            }
        }
    }
}
""",
    "src/main/java/com/vortexos/weathersync/WeatherParticleSystem.kt": """package com.vortexos.weathersync

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.random.Random

class WeatherParticleSystem {
    private var width = 0
    private var height = 0
    private var weatherType = WeatherType.UNKNOWN
    private var density = 100
    
    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun setSize(w: Int, h: Int) {
        width = w
        height = h
        recreateParticles()
    }

    fun setWeatherType(type: WeatherType) {
        weatherType = type
        recreateParticles()
    }
    
    fun setDensity(d: Int) {
        density = d
        recreateParticles()
    }

    private fun recreateParticles() {
        particles.clear()
        if (width == 0 || height == 0) return
        
        val count = density
        for (i in 0 until count) {
            particles.add(createParticle())
        }
    }

    private fun createParticle(): Particle {
        return when (weatherType) {
            WeatherType.SUNNY -> SunnyParticle(width, height)
            WeatherType.RAINY, WeatherType.STORMY -> RainParticle(width, height)
            WeatherType.SNOWY -> SnowParticle(width, height)
            WeatherType.NIGHT -> StarParticle(width, height)
            else -> CloudParticle(width, height)
        }
    }

    fun update() {
        for (p in particles) {
            p.update(width, height)
        }
    }

    fun draw(canvas: Canvas) {
        // Background
        val bgColor = when (weatherType) {
            WeatherType.SUNNY -> Color.parseColor("#87CEEB") // Sky blue
            WeatherType.RAINY -> Color.parseColor("#455A64")
            WeatherType.STORMY -> Color.parseColor("#263238")
            WeatherType.SNOWY -> Color.parseColor("#CFD8DC")
            WeatherType.NIGHT -> Color.parseColor("#121212")
            WeatherType.SUNSET -> Color.parseColor("#FF8A65")
            else -> Color.parseColor("#90A4AE")
        }
        canvas.drawColor(bgColor)
        
        // Draw particles
        for (p in particles) {
            p.draw(canvas, paint)
        }
        
        // Storm lightning
        if (weatherType == WeatherType.STORMY && Random.nextFloat() < 0.02f) {
            canvas.drawColor(Color.argb(100, 255, 255, 255))
        }
    }
}

abstract class Particle(val screenW: Int, val screenH: Int) {
    var x = Random.nextFloat() * screenW
    var y = Random.nextFloat() * screenH
    var speed = 0f
    var size = 0f
    
    abstract fun update(w: Int, h: Int)
    abstract fun draw(canvas: Canvas, paint: Paint)
}

class RainParticle(w: Int, h: Int) : Particle(w, h) {
    init {
        speed = 15f + Random.nextFloat() * 10f
        size = 2f + Random.nextFloat() * 2f
    }
    
    override fun update(w: Int, h: Int) {
        y += speed
        if (y > h) {
            y = 0f
            x = Random.nextFloat() * w
        }
    }
    
    override fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.parseColor("#80FFFFFF")
        paint.strokeWidth = size
        canvas.drawLine(x, y, x, y + size * 4, paint)
    }
}

class SnowParticle(w: Int, h: Int) : Particle(w, h) {
    var drift = Random.nextFloat() * 2 - 1
    
    init {
        speed = 2f + Random.nextFloat() * 3f
        size = 5f + Random.nextFloat() * 10f
    }
    
    override fun update(w: Int, h: Int) {
        y += speed
        x += drift
        if (y > h) {
            y = 0f
            x = Random.nextFloat() * w
        }
        if (x > w) x = 0f
        if (x < 0) x = w.toFloat()
    }
    
    override fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.WHITE
        canvas.drawCircle(x, y, size, paint)
    }
}

class StarParticle(w: Int, h: Int) : Particle(w, h) {
    var twinkle = Random.nextFloat() * 255
    var twinkleDir = if (Random.nextBoolean()) 5 else -5
    
    init {
        size = 1f + Random.nextFloat() * 4f
    }
    
    override fun update(w: Int, h: Int) {
        twinkle += twinkleDir
        if (twinkle > 255) {
            twinkle = 255f
            twinkleDir = -5
        } else if (twinkle < 50) {
            twinkle = 50f
            twinkleDir = 5
        }
    }
    
    override fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.argb(twinkle.toInt(), 255, 255, 255)
        canvas.drawCircle(x, y, size, paint)
    }
}

class SunnyParticle(w: Int, h: Int) : Particle(w, h) {
    init {
        size = 10f + Random.nextFloat() * 20f
        speed = 0.5f + Random.nextFloat()
    }
    
    override fun update(w: Int, h: Int) {
        y -= speed
        if (y < -size) {
            y = h + size
            x = Random.nextFloat() * w
        }
    }
    
    override fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.argb(40, 255, 235, 59)
        canvas.drawCircle(x, y, size, paint)
    }
}

class CloudParticle(w: Int, h: Int) : Particle(w, h) {
    init {
        size = 100f + Random.nextFloat() * 200f
        speed = 0.2f + Random.nextFloat() * 0.5f
    }
    
    override fun update(w: Int, h: Int) {
        x += speed
        if (x > w + size) {
            x = -size
            y = Random.nextFloat() * h
        }
    }
    
    override fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.argb(50, 255, 255, 255)
        canvas.drawCircle(x, y, size, paint)
    }
}
""",
    "src/main/java/com/vortexos/weathersync/WeatherApiClient.kt": """package com.vortexos.weathersync

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
""",
    "src/main/java/com/vortexos/weathersync/WeatherSyncManager.kt": """package com.vortexos.weathersync

import android.content.Context
import android.content.Intent
import android.location.Location
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
"""
}

for rel_path, content in files.items():
    if rel_path == "src/main/layout/activity_settings.xml":
        rel_path = "src/main/res/layout/activity_settings.xml" # Fix path
    
    full_path = os.path.join(base_dir, rel_path.replace("/", "\\"))
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)

print("All files generated successfully.")
