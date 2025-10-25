package com.example.bodyblissapp.data.model

data class SignResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val error: String? = null,
    val role: String? = null,
    val name: String? = null,
    val email: String? = null
)