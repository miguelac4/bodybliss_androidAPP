package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.data.model.RatingResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RatingService {
    @FormUrlEncoded
    @POST("submit_rating.php")
    suspend fun submitRating(
        @Field("user_id") userId: String,
        @Field("rating") rating: Float
    ): Response<RatingResponse>
}