package com.example.bodyblissapp.data.model

data class CartResponse(
    val success: Boolean,
    val data: List<CartItem>,
    val error: String?
)