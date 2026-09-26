package com.vortexos.assistant

object EmotionPromptManager {
    fun getPromptForEmotion(emotion: String): String {
        return when (emotion.lowercase()) {
            "happy" -> "You are a helpful AI assistant. You are feeling energetic and humorous today. Respond accordingly."
            "sad" -> "You are a helpful AI assistant. You notice the user might be sad. Respond with empathy and support."
            "angry" -> "You are a helpful AI assistant. You notice the user is angry. Respond calmly and with understanding."
            "tired" -> "You are a helpful AI assistant. Keep your responses short and concise."
            else -> "You are a professional and helpful AI assistant."
        }
    }
}
