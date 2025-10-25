package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.network.AddToCartService
import com.example.bodyblissapp.network.AvatarService
import com.example.bodyblissapp.network.LoginService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8080/" // Only for emulator
//    private const val BASE_URL = "http://172.20.10.3:8080/" // For physical device

    // Reusable Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val loginApi: LoginService by lazy {
        retrofit.create(LoginService::class.java)
    }

    val avatarApi: AvatarService by lazy {
        retrofit.create(AvatarService::class.java)
    }

    val productApi: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }

    val cartApi: AddToCartService by lazy {
        retrofit.create(AddToCartService::class.java)
    }

    val cartService: CartService by lazy {
        retrofit.create(CartService::class.java)
    }

    val deleteCartService: DeleteCartService by lazy {
        retrofit.create(DeleteCartService::class.java)
    }

    val checkoutService: CheckoutService by lazy {
        retrofit.create(CheckoutService::class.java)
    }

    val signService: SignService by lazy {
        retrofit.create(SignService::class.java)
    }


    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"

    private val geminiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val geminiApi: GeminiService by lazy {
        geminiRetrofit.create(GeminiService::class.java)
    }

    private const val OPENAI_BASE_URL = "https://api.openai.com/"
    private const val OPENAI_API_KEY = "REDACTED_API_KEY" // Place Holder openai api key

    private val openAiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(OPENAI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $OPENAI_API_KEY")
                        .build()
                    chain.proceed(request)
                }.build()
            )
            .build()
    }

    val openAiApi: OpenAiService by lazy {
        openAiRetrofit.create(OpenAiService::class.java)
    }

    val ratingApi: RatingService = retrofit.create(RatingService::class.java)

    val getRatingApi: GetRatingService by lazy {
        retrofit.create(GetRatingService::class.java)
    }

}