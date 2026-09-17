package com.tubitak.themeengine.wallpaper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.tubitak.themeengine.modes.*

/**
 * Tubitak Live Wallpaper Service.
 * Seçilen tema moduna göre duvar kağıdını periyodik olarak günceller.
 */
class TubitakWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return TubitakEngine()
    }

    inner class TubitakEngine : Engine() {

        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private var currentBitmap: Bitmap? = null
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        private val updateRunnable = object : Runnable {
            override fun run() {
                if (visible) {
                    drawFrame()
                    // Her 15 dakikada bir güncelle
                    handler.postDelayed(this, 15 * 60 * 1000L)
                }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(updateRunnable)
            } else {
                handler.removeCallbacks(updateRunnable)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(updateRunnable)
        }

        private fun drawFrame() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null && currentBitmap != null) {
                    drawBitmap(canvas, currentBitmap!!)
                }
            } finally {
                canvas?.let { holder.unlockCanvasAndPost(it) }
            }
        }

        private fun drawBitmap(canvas: Canvas, bitmap: Bitmap) {
            val screenWidth = canvas.width.toFloat()
            val screenHeight = canvas.height.toFloat()
            val bitmapRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val screenRatio = screenWidth / screenHeight

            val destRect = if (bitmapRatio > screenRatio) {
                val scaledHeight = screenWidth / bitmapRatio
                val top = (screenHeight - scaledHeight) / 2
                Rect(0, top.toInt(), screenWidth.toInt(), (top + scaledHeight).toInt())
            } else {
                val scaledWidth = screenHeight * bitmapRatio
                val left = (screenWidth - scaledWidth) / 2
                Rect(left.toInt(), 0, (left + scaledWidth).toInt(), screenHeight.toInt())
            }

            canvas.drawBitmap(bitmap, null, destRect, paint)
        }

        fun setBitmap(bitmap: Bitmap) {
            currentBitmap?.recycle()
            currentBitmap = bitmap
            if (visible) {
                handler.post { drawFrame() }
            }
        }
    }
}
