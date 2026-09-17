package com.tubitak.themeengine

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tubitak.themeengine.databinding.ActivityMainBinding
import com.tubitak.themeengine.modes.*
import com.tubitak.themeengine.wallpaper.TubitakWallpaperService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var emotionSettings: EmotionSettings
    private var faceEmotionDetector: FaceEmotionDetector? = null

    private var selectedImageUri: Uri? = null
    private var lastKnownLocation: Location? = null
    private var isFaceDetectionRunning = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.tvSelectedImage.text = getString(R.string.image_selected)
        }
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            toggleFaceDetection(true)
        } else {
            Toast.makeText(this, "Kamera izni gerekli", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        emotionSettings = EmotionSettings(this)

        setupThemeModeSpinner()
        setupEmotionSpinner()
        setupButtons()
        updateFaceDetectionButton()
        checkApiKeys()
    }

    override fun onDestroy() {
        super.onDestroy()
        faceEmotionDetector?.stopDetection()
    }

    private fun setupThemeModeSpinner() {
        val modes = ThemeMode.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerThemeMode.adapter = adapter
    }

    private val spinnerEmotions = listOf(
        EmotionTheme.Emotion.HAPPY,
        EmotionTheme.Emotion.ANGRY,
        EmotionTheme.Emotion.SAD,
        EmotionTheme.Emotion.CALM
    )

    private fun setupEmotionSpinner() {
        val emotions = spinnerEmotions.map { emotionName(it) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, emotions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEmotion.adapter = adapter
    }

    private fun emotionName(emotion: EmotionTheme.Emotion): String {
        return when (emotion) {
            EmotionTheme.Emotion.HAPPY -> "Mutlu"
            EmotionTheme.Emotion.ANGRY -> "Sinirli"
            EmotionTheme.Emotion.SAD -> "Üzgün"
            EmotionTheme.Emotion.CALM -> "Sakin"
            EmotionTheme.Emotion.UNKNOWN -> "Bilinmiyor"
        }
    }

    private fun setupButtons() {
        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnRefreshLocation.setOnClickListener {
            requestLocationAndApply()
        }

        binding.btnApplyWallpaper.setOnClickListener {
            applySelectedTheme()
        }

        binding.btnSetLiveWallpaper.setOnClickListener {
            setLiveWallpaper()
        }

        binding.btnToggleFaceDetection.setOnClickListener {
            if (isFaceDetectionRunning) {
                toggleFaceDetection(false)
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnEmotionSettings.setOnClickListener {
            showEmotionSettingsDialog()
        }
    }

    private fun updateFaceDetectionButton() {
        val text = if (isFaceDetectionRunning) {
            "Yüzden Duygu Tespiti: AÇIK"
        } else {
            "Yüzden Duygu Tespiti: KAPALI"
        }
        binding.btnToggleFaceDetection.text = text
        binding.previewView.visibility = if (isFaceDetectionRunning) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun toggleFaceDetection(start: Boolean) {
        if (start) {
            emotionSettings.isEnabled = true
            faceEmotionDetector = FaceEmotionDetector(this).apply {
                startDetection(
                    this@MainActivity,
                    binding.previewView,
                    object : FaceEmotionDetector.EmotionCallback {
                        override fun onEmotionDetected(emotion: EmotionTheme.Emotion) {
                            runOnUiThread {
                                binding.tvCurrentEmotion.text = "Duygu: ${emotionName(emotion)}"
                                if (binding.spinnerThemeMode.selectedItemPosition == ThemeMode.EMOTION.ordinal) {
                                    applyEmotionTheme(emotion)
                                }
                            }
                        }

                        override fun onError(error: String) {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
            isFaceDetectionRunning = true
        } else {
            faceEmotionDetector?.stopDetection()
            faceEmotionDetector = null
            emotionSettings.isEnabled = false
            isFaceDetectionRunning = false
        }
        updateFaceDetectionButton()
    }

    private fun showEmotionSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_emotion_settings, null)
        val seekBarSensitivity = dialogView.findViewById<SeekBar>(R.id.seekBarSensitivity)
        seekBarSensitivity.progress = (emotionSettings.sensitivity * 100).toInt()

        AlertDialog.Builder(this)
            .setTitle("Duygu Tespiti Ayarları")
            .setView(dialogView)
            .setPositiveButton("Kaydet") { _, _ ->
                emotionSettings.sensitivity = seekBarSensitivity.progress / 100f
                Toast.makeText(this, "Hassasiyet: ${emotionSettings.sensitivity}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun checkApiKeys() {
        val openWeatherKey = getString(R.string.openweather_api_key)

        if (openWeatherKey == "YOUR_OPENWEATHER_API_KEY") {
            AlertDialog.Builder(this)
                .setTitle("API Anahtarı Gerekli")
                .setMessage("Hava durumu teması için OpenWeatherMap API anahtarı gerekli. " +
                        "Konum teması şu an ücretsiz OpenStreetMap kullandığı için Google Maps key gerekmez.")
                .setPositiveButton("Tamam", null)
                .show()
        }
    }

    private fun applySelectedTheme() {
        val selectedMode = ThemeMode.values()[binding.spinnerThemeMode.selectedItemPosition]

        when (selectedMode) {
            ThemeMode.PERSONAL -> applyPersonalTheme()
            ThemeMode.WEATHER -> applyWeatherTheme()
            ThemeMode.EMOTION -> applyEmotionThemeFromSpinner()
            ThemeMode.LOCATION -> applyLocationTheme()
            ThemeMode.TIME -> applyTimeTheme()
        }
    }

    private fun applyPersonalTheme() {
        val uri = selectedImageUri
        if (uri == null) {
            Toast.makeText(this, "Lütfen önce bir fotoğraf seçin", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = PersonalTheme(this).getWallpaperFromUri(uri)
        bitmap?.let {
            setWallpaperBitmap(it)
        } ?: Toast.makeText(this, "Fotoğraf yüklenemedi", Toast.LENGTH_SHORT).show()
    }

    private fun applyWeatherTheme() {
        val location = lastKnownLocation
        if (location == null) {
            requestLocationAndApply(ThemeMode.WEATHER)
            return
        }

        lifecycleScope.launch {
            val weather = WeatherTheme(this@MainActivity).fetchWeather(location)
            val bitmap = weather?.wallpaperUrl?.let { WeatherTheme(this@MainActivity).downloadBitmap(it) }
            bitmap?.let { setWallpaperBitmap(it) }
                ?: Toast.makeText(this@MainActivity, "Hava durumu teması yüklenemedi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyEmotionThemeFromSpinner() {
        val emotion = spinnerEmotions[binding.spinnerEmotion.selectedItemPosition]
        applyEmotionTheme(emotion)
    }

    private fun applyEmotionTheme(emotion: EmotionTheme.Emotion) {
        val result = EmotionTheme(this).getThemeForEmotion(emotion)

        lifecycleScope.launch {
            val bitmap = EmotionTheme(this@MainActivity).downloadBitmap(result.wallpaperUrl)
            bitmap?.let {
                setWallpaperBitmap(it)
                // Sistem renklerini değiştirmek için OS seviyesinde overlay gerekir.
                // Şimdilik sadece arka plan değişir; sistem renkleri için
                // TubitakThemeEngine system app olarak Bliss OS'a gömüldüğünde
                // monet/overlay API kullanılabilir.
            } ?: Toast.makeText(this@MainActivity, "Duygu teması yüklenemedi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyLocationTheme() {
        val location = lastKnownLocation
        if (location == null) {
            requestLocationAndApply(ThemeMode.LOCATION)
            return
        }

        val theme = LocationTheme(this)
        val url = theme.buildMapUrl(location)

        lifecycleScope.launch {
            val bitmap = theme.downloadMapImage(url)
            bitmap?.let { setWallpaperBitmap(it) }
                ?: Toast.makeText(this@MainActivity, "Konum teması yüklenemedi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyTimeTheme() {
        val location = lastKnownLocation ?: run {
            val fallbackLocation = Location("").apply {
                latitude = 41.0082
                longitude = 28.9784 // İstanbul varsayılan
            }
            fallbackLocation
        }

        val timeTheme = TimeTheme(this)
        val result = timeTheme.getThemeForLocation(location)

        lifecycleScope.launch {
            val bitmap = timeTheme.downloadBitmap(result.wallpaperUrl)
            bitmap?.let { setWallpaperBitmap(it) }
                ?: Toast.makeText(this@MainActivity, "Saat teması yüklenemedi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setWallpaperBitmap(bitmap: Bitmap) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(this)
            wallpaperManager.setBitmap(bitmap)
            Toast.makeText(this, "Duvar kağıdı uygulandı", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Duvar kağıdı ayarlanamadı", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLiveWallpaper() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, TubitakWallpaperService::class.java)
            )
        }
        startActivity(intent)
    }

    private fun requestLocationAndApply(mode: ThemeMode? = null) {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> {
                fetchLocation(mode)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(this)
                    .setTitle("Konum İzni Gerekli")
                    .setMessage("Hava durumu, konum ve saat temaları için konum bilginize ihtiyacımız var.")
                    .setPositiveButton("İzin Ver") { _, _ -> requestLocationPermission() }
                    .setNegativeButton("İptal", null)
                    .show()
            }
            else -> requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun fetchLocation(afterMode: ThemeMode? = null) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                lastKnownLocation = location
                if (location != null) {
                    Toast.makeText(this, "Konum alındı", Toast.LENGTH_SHORT).show()
                    afterMode?.let { applyThemeAfterLocation(it) }
                } else {
                    Toast.makeText(this, "Konum alınamadı, GPS açık olduğundan emin olun", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Konum hatası: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun applyThemeAfterLocation(mode: ThemeMode) {
        when (mode) {
            ThemeMode.WEATHER -> applyWeatherTheme()
            ThemeMode.LOCATION -> applyLocationTheme()
            ThemeMode.TIME -> applyTimeTheme()
            else -> { /* no-op */ }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                Toast.makeText(this, "Konum izni verilmedi", Toast.LENGTH_SHORT).show()
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder(this)
                        .setTitle("İzin Gerekli")
                        .setMessage("Konum iznini manuel olarak ayarlardan vermelisiniz.")
                        .setPositiveButton("Ayarlar") { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", packageName, null)
                            }
                            startActivity(intent)
                        }
                        .setNegativeButton("İptal", null)
                        .show()
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
