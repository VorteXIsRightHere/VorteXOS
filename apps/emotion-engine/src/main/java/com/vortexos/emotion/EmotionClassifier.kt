package com.vortexos.emotion

import com.google.mlkit.vision.face.Face

object EmotionClassifier {
    
    const val EMOTION_HAPPY = "happy"
    const val EMOTION_SAD = "sad"
    const val EMOTION_ANGRY = "angry"
    const val EMOTION_SURPRISED = "surprised"
    const val EMOTION_NEUTRAL = "neutral"
    const val EMOTION_TIRED = "tired"
    const val EMOTION_RELAXED = "relaxed"

    fun classifyEmotion(face: Face): String {
        val smileProb = face.smilingProbability ?: 0f
        val rightEyeOpen = face.rightEyeOpenProbability ?: 0.5f
        val leftEyeOpen = face.leftEyeOpenProbability ?: 0.5f
        val avgEyeOpen = (rightEyeOpen + leftEyeOpen) / 2f
        val headEulerY = face.headEulerAngleY // Left-right
        val headEulerZ = face.headEulerAngleZ // Tilt

        if (smileProb > 0.7f) return EMOTION_HAPPY
        if (smileProb < 0.3f && avgEyeOpen < 0.3f) return EMOTION_TIRED
        if (avgEyeOpen > 0.9f && smileProb < 0.3f) return EMOTION_SURPRISED
        if (smileProb in 0.3f..0.7f && Math.abs(headEulerZ) < 5f) return EMOTION_RELAXED
        if (smileProb < 0.2f && avgEyeOpen > 0.4f && Math.abs(headEulerZ) > 5f) return EMOTION_SAD
        if (smileProb < 0.2f && Math.abs(headEulerY) > 15f) return EMOTION_ANGRY
        
        return EMOTION_NEUTRAL
    }
}
