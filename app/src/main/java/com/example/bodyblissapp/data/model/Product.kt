package com.example.bodyblissapp.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,

    @SerializedName("product_name")
    val name: String,

    @SerializedName("product_description")
    val description: String,

    val price: Float,

    @SerializedName("product_image_url")
    val productImageUrl: String,

    @SerializedName("category_name")
    val category: String,

    @SerializedName("category_image_url")
    val categoryImageUrl: String
)
