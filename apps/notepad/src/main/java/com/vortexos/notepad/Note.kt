package com.vortexos.notepad

data class Note(
    val id: Long = -1,
    var title: String,
    var content: String,
    var category: String,
    val timestamp: Long,
    var colorTag: Int
)
