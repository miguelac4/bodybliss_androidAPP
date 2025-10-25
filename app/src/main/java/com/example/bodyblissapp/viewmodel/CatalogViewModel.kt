package com.example.bodyblissapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.model.Product
import com.example.bodyblissapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class CatalogViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchProducts()
    }

    fun fetchProducts(language: String = Locale.getDefault().language) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _products.value = RetrofitInstance.productApi.getProducts(language)
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar os produtos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
