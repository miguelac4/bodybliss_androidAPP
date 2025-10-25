package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.data.model.OpenAiRequest
import com.example.bodyblissapp.data.model.OpenAiResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiService {
    @Headers(
        "Content-Type: application/json"
    )
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Body request: OpenAiRequest
    ): OpenAiResponse
}
