package com.example.bodyblissapp.data.network

import com.example.bodyblissapp.data.model.Product
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {
    @GET("products.php")
    suspend fun getProducts(@Query("lang") lang: String): List<Product>

}
