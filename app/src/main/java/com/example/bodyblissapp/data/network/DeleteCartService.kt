package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.data.model.AddToCartRequest
import com.example.bodyblissapp.data.model.GenericResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface DeleteCartService {
    @FormUrlEncoded
    @POST("delete_from_cart.php")
    suspend fun deleteFromCart(
        @Field("user_id") userId : Int,
        @Field("product_id") productId : Int
    ): GenericResponse
}

