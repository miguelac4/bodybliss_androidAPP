package com.example.bodyblissapp.network

import com.example.bodyblissapp.data.model.AddToCartRequest
import com.example.bodyblissapp.data.model.GenericResponse
import retrofit2.http.*

interface AddToCartService {

    @Headers("Content-Type: application/json")
    @POST("add_to_cart.php")
    suspend fun addToCart(@Body request: AddToCartRequest): GenericResponse
}