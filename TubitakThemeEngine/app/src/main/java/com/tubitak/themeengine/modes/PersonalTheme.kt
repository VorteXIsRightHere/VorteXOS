package com.tubitak.themeengine.modes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

/**
 * Kişisel Tema: Kullanıcının galeriden seçtiği fotoğrafı duvar kağıdı olarak kullanır.
 */
class PersonalTheme(private val context: Context) {

    fun getWallpaperFromUri(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
