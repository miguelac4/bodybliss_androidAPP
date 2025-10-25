package com.example.bodyblissapp.data.model

data class SignRequest(
    val user_id: Int,
    val lang: String = "en",
    val card_number: String,
    val expirate_date: String,
    val cvc_cvv: String,
    val name_on_card: String
)