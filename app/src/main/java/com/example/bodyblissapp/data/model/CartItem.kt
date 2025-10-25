package com.example.bodyblissapp.data.model

data class CartItem(
    val user_id: Int,
    val product_id: Int,
    val quantity: Int,
    val name: String,
    val price: Double
)