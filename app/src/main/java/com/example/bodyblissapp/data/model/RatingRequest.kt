package com.example.bodyblissapp.data.model

import com.google.gson.annotations.SerializedName

data class RatingRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("rating") val rating: Float
)