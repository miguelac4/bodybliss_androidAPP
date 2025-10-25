package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.data.model.AverageRatingResponse
import retrofit2.Response
import retrofit2.http.GET

interface GetRatingService {
    @GET("get_average_rate.php")
    suspend fun getAverageRating(): Response<AverageRatingResponse>
}