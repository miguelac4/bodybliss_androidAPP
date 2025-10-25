package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.data.model.CartResponse
import retrofit2.http.*

interface CartService {
    @FormUrlEncoded
    @POST("view_cart.php") // Use your real file name here
    suspend fun getCartItems(
        @Field("user_id") userId: Int,
        @Field("lang") lang: String = "en"
    ): CartResponse
}