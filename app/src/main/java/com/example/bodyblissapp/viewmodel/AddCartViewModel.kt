package com.example.bodyblissapp.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.model.AddToCartRequest
import com.example.bodyblissapp.data.network.RetrofitInstance
import com.example.bodyblissapp.util.SessionManager
import kotlinx.coroutines.launch

class AddCartViewModel : ViewModel() {
    private val _addErrorMap = mutableStateMapOf<Int, String?>()
    val addErrorMap: Map<Int, String?> get() = _addErrorMap

    fun addProductToCart(context: Context, productId: Int, quantity: Int = 1) {
        val sessionManager = SessionManager(context)
        val userId = sessionManager.getUserId()

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.cartApi.addToCart(
                    AddToCartRequest(user_id = userId, product_id = productId, quantity = quantity))
                if (response.success) {
                    _addErrorMap[productId] = null
                    Toast.makeText(context, "Adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
                } else {
                    _addErrorMap[productId] = response.message ?: "Erro ao adicionar ao carrinho."
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Network error", e)
                _addErrorMap[productId] = e.localizedMessage ?: "Erro desconhecido"
            }
        }
    }
}
