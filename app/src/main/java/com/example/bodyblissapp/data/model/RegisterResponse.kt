package com.example.bodyblissapp.data.model

data class RegisterResponse(
    val success: Boolean,
    val error: String?,
    val user_id: Int?,
    val message: String?
)
