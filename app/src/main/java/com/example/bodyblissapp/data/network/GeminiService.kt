package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.data.model.GeminiRequest
import com.example.bodyblissapp.data.model.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiService {
    @POST("models/gemini-pro:generateContent")
    suspend fun getSuggestion(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
