package com.vortexos.assistant

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
        
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey

    fun generateResponse(prompt: String, emotion: String): String {
        val systemPrompt = EmotionPromptManager.getPromptForEmotion(emotion)
        val fullPrompt = systemPrompt + "\nUser: " + prompt
        
        val json = JSONObject()
        val contents = JSONArray()
        val content = JSONObject()
        val parts = JSONArray()
        val part = JSONObject()
        
        part.put("text", fullPrompt)
        parts.put(part)
        content.put("parts", parts)
        contents.put(content)
        json.put("contents", contents)

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return "Error: ${response.code}"
                val responseBody = response.body?.string() ?: return "Empty response"
                val responseJson = JSONObject(responseBody)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.optJSONObject("content")
                    val partsArr = contentObj?.optJSONArray("parts")
                    if (partsArr != null && partsArr.length() > 0) {
                        return partsArr.getJSONObject(0).optString("text")
                    }
                }
                return "Could not parse response."
            }
        } catch (e: IOException) {
            return "Network Error: ${e.message}"
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
    }
}
