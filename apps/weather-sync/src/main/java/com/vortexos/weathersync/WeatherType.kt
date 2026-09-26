package com.vortexos.weathersync

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
