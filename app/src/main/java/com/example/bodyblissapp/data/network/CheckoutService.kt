package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.data.model.CheckoutResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CheckoutService {

    @FormUrlEncoded
    @POST("checkout.php")
    suspend fun checkout(
        @Field("user_id") userId: Int,
        @Field("card_number") cardNumber: String,
        @Field("expirate_date") expiry: String,
        @Field("cvc_cvv") cvc: String,
        @Field("name_on_card") cardName: String,
        @Field("lang") lang: String = "en"
    ): CheckoutResponse
}