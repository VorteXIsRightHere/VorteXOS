package com.vortexos.security

import android.content.Context
import android.content.SharedPreferences

object AppLockManager {
    private const val PREFS_NAME = "AppLockPrefs"
    private const val KEY_LOCKED_APPS = "LockedApps"

    private lateinit val prefs: SharedPreferences

    fun init(context: Context) {
        if (!this::prefs.isInitialized) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun lockApp(packageName: String) {
        val lockedApps = getLockedApps().toMutableSet()
        lockedApps.add(packageName)
        prefs.edit().putStringSet(KEY_LOCKED_APPS, lockedApps).apply()
    }

    fun unlockApp(packageName: String) {
        val lockedApps = getLockedApps().toMutableSet()
        lockedApps.remove(packageName)
        prefs.edit().putStringSet(KEY_LOCKED_APPS, lockedApps).apply()
    }

    fun isLocked(packageName: String): Boolean {
        return getLockedApps().contains(packageName)
    }

    fun getLockedApps(): Set<String> {
        return prefs.getStringSet(KEY_LOCKED_APPS, emptySet()) ?: emptySet()
    }
}
