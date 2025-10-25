package com.example.bodyblissapp.data.ai

import com.example.bodyblissapp.data.ai.GeminiApi.URL
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object GeminiApi {
    private const val API_KEY = BuildConfig.GEMINI_API_KEY
    private const val URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=$API_KEY"

    suspend fun sendPrompt(prompt: String): String? {
        // Usa Retrofit ou HttpURLConnection + kotlinx.serialization/ Gson
        // Simples pseudocódigo:
        val json = """
            {
                "contents": [{"parts": [{"text": "$prompt"}]}]
            }
        """.trimIndent()

        val connection = URL(URL).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.outputStream.write(json.toByteArray())

        return if (connection.responseCode == 200) {
            val response = connection.inputStream.bufferedReader().readText()
            extractTextFromGeminiResponse(response)
        } else null
    }

    private fun extractTextFromGeminiResponse(response: String): String {
        // Extrai o texto com base no formato do Gemini
        val jsonObj = JSONObject(response)
        return jsonObj
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
    }
}
