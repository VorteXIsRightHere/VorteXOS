// Top-level build file for VorteXOS
// Bu dosya tüm alt modüller için ortak yapılandırma sağlar

plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

// Tüm projeler için ortak ayarlar
allprojects {
    // VorteXOS sürüm bilgileri
    extra["vortexosVersionCode"] = 1
    extra["vortexosVersionName"] = "1.0.0"
    extra["vortexosCompileSdk"] = 33
    extra["vortexosTargetSdk"] = 30
    extra["vortexosMinSdk"] = 28
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
