package com.example.bodyblissapp.data.model

data class AvatarResponse(
    val success: Boolean,
    val image_url: String? = null,
    val error: String? = null
)
