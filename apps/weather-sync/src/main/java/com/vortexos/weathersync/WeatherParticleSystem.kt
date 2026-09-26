package com.vortexos.weathersync

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
