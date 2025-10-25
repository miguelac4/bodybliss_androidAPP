package com.example.bodyblissapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.model.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SearchViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    val searchQuery = MutableStateFlow("")
    val minPrice = MutableStateFlow<Float?>(null)
    val maxPrice = MutableStateFlow<Float?>(null)

    val filteredProducts = combine(products, searchQuery, minPrice, maxPrice) { all, query, min, max ->
        all.filter { product ->
            val matchesName = query.isBlank() || product.name.contains(query, ignoreCase = true)
            val matchesMin = min == null || product.price >= min
            val matchesMax = max == null || product.price <= max
            matchesName && matchesMin && matchesMax
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(query: String) {
        searchQuery.value = query
    }

    fun updateMinPrice(value: Float?) {
        minPrice.value = value
    }

    fun updateMaxPrice(value: Float?) {
        maxPrice.value = value
    }

    fun fetchProducts(lang: String = "en") {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val url = URL("http://10.0.2.2:8080/products.php?lang=$lang")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"

                    if (connection.responseCode == 200) {
                        connection.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        Log.e("SearchViewModel", "HTTP Error: ${connection.responseCode}")
                        null
                    }
                }

                result?.let {
                    val json = Gson().fromJson(it, Array<Product>::class.java).toList()
                    _products.value = json
                }

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Fetch failed", e)
            }
        }
    }

}

