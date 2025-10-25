package com.example.bodyblissapp.data.model

data class LoginResponse(
    val success: Boolean,
    val error: String?,
    val user_id: Int?,
    val name: String? = null,
    val role: String?,
    val email: String? = null
)