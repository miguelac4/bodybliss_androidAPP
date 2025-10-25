package com.example.bodyblissapp.data.model

data class CheckoutRequest(
    val userId: Int,
    val name: String,
    val number: String,
    val expiry: String,
    val cvc: String,
    val lang: String = "en"
)