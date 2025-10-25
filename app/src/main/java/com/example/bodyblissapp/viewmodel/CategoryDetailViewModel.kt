package com.example.bodyblissapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.model.Product
import com.example.bodyblissapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class CategoryDetailViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadProductsForCategory(categoria: String, language: String = Locale.getDefault().language) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val allProducts = RetrofitInstance.productApi.getProducts(language)
                _products.value = allProducts.filter {
                    it.category.equals(categoria, ignoreCase = true)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Erro desconhecido"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
