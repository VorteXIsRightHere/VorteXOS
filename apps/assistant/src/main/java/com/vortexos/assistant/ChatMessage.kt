package com.vortexos.assistant

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val id: Long = 0
)
