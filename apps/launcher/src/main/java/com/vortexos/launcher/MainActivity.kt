package com.vortexos.launcher

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.vortexos.common.VorteXThemeManager
import com.vortexos.launcher.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), VorteXThemeManager.OnThemeChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var themeManager: VorteXThemeManager

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeManager = VorteXThemeManager(this)

        setupGestures()
        setupDock()
        setupDrawer()
        applyDynamicTheme(themeManager.getMergedAccentColor())

        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun setupGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                // Screen lock — requires DeviceAdmin; simulated here
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                val deltaY = e2.y - e1.y
                return when {
                    deltaY > 100 -> {
                        // Swipe down → expand notification panel
                        @SuppressLint("WrongConstant")
                        val statusBarService = getSystemService("statusbar")
                        try {
                            val cls = Class.forName("android.app.StatusBarManager")
                            cls.getMethod("expandNotificationsPanel").invoke(statusBarService)
                        } catch (_: Exception) {}
                        true
                    }
                    deltaY < -100 -> {
                        openAppDrawer()
                        true
                    }
                    else -> false
                }
            }
        })
    }

    private fun setupDock() {
        val pm: PackageManager = packageManager
        val dockPackages = listOf(
            "com.android.dialer",
            "com.android.chrome",
            "com.android.camera2",
            "com.vortexos.filemanager",
            "com.vortexos.settings"
        )

        binding.dockBar.removeAllViews()
        for (pkg in dockPackages) {
            val icon = try {
                pm.getApplicationIcon(pkg)
            } catch (_: PackageManager.NameNotFoundException) {
                getDrawable(android.R.drawable.sym_def_app_icon)
            }

            val iconView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                setPadding(16, 16, 16, 16)
                setImageDrawable(icon)
                setOnClickListener {
                    try {
                        pm.getLaunchIntentForPackage(pkg)?.let { startActivity(it) }
                    } catch (_: Exception) {}
                }
            }
            binding.dockBar.addView(iconView)
        }
    }

    private fun setupDrawer() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.drawerContainer, AppDrawerFragment())
            .commit()
    }

    private fun openAppDrawer() {
        binding.drawerContainer.visibility = View.VISIBLE
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerContainer.visibility == View.VISIBLE) {
            binding.drawerContainer.visibility = View.GONE
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    private fun applyDynamicTheme(color: Int) {
        binding.searchBar.setCardBackgroundColor(
            Color.argb(180, Color.red(color), Color.green(color), Color.blue(color))
        )
        window.statusBarColor = VorteXThemeManager.darkenColor(color, 0.3f)
    }

    // ── VorteXThemeManager.OnThemeChangedListener ──
    override fun onAccentColorChanged(color: Int) = applyDynamicTheme(color)
    override fun onWeatherChanged(weatherType: String, accentColor: Int) {}
    override fun onEmotionChanged(emotionType: String, accentColor: Int) {}

    override fun onResume() {
        super.onResume()
        themeManager.register(this)
    }

    override fun onPause() {
        super.onPause()
        themeManager.unregister()
    }
}
