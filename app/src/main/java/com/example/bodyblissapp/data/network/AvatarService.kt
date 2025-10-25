package com.example.bodyblissapp.network

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.bodyblissapp.data.model.AvatarResponse
import okhttp3.MultipartBody
import okhttp3.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AvatarService {
    @GET("get_avatar.php")
    suspend fun getUserAvatar(
        @Query("user_id") userId: Int
    ): AvatarResponse

    @Multipart
    @POST("upload_avatar.php")
    suspend fun uploadAvatar(
        @Part avatar: MultipartBody.Part,
        @Part("user_id") userId: Int
    ): AvatarResponse

    @FormUrlEncoded
    @POST("remove_avatar.php")
    suspend fun removeAvatar(
        @Field("user_id") userId: Int
    ): AvatarResponse
}
