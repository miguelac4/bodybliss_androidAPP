package com.example.bodyblissapp.network
import com.example.bodyblissapp.data.model.LoginResponse
import com.example.bodyblissapp.data.model.RegisterResponse
import retrofit2.http.*

interface LoginService {
    @FormUrlEncoded
    @POST("login.php")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register.php")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("gender") gender: String
    ): RegisterResponse
}
