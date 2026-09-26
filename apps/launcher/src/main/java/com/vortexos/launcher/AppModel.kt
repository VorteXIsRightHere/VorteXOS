package com.vortexos.launcher

import android.graphics.drawable.Drawable
import android.content.Intent

data class AppModel(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val intent: Intent
)
