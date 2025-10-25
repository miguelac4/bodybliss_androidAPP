package com.example.bodyblissapp.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,

    @SerializedName("name")
    val name: String,

    val image: String
)
