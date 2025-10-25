package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.data.model.SignResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignService {

    @FormUrlEncoded
    @POST("sign_bodybliss_plus.php")
    suspend fun signUpVip(
        @Field("user_id") userId: Int,
        @Field("lang") lang: String = "en",
        @Field("card_number") cardNumber: String,
        @Field("expirate_date") expirateDate: String,
        @Field("cvc_cvv") cvcCvv: String,
        @Field("name_on_card") nameOnCard: String
    ): Response<SignResponse>
}