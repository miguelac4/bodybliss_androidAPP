package com.example.bodyblissapp.data.model

data class AddToCartRequest(
    val user_id: Int,
    val product_id: Int,
    val quantity: Int = 1
)