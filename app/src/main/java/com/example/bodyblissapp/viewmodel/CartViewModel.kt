package com.example.bodyblissapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.model.CartItem
import com.example.bodyblissapp.data.network.RetrofitInstance
import com.example.bodyblissapp.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchCart(context: Context) {
        val userId = SessionManager(context).getUserId() ?: return
        val lang = Locale.getDefault().language

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.cartService.getCartItems(userId, lang)
                if (response.success) {
                    _cartItems.value = response.data
                    _error.value = null
                } else {
                    _error.value = response.error ?: "Erro desconhecido"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Erro de rede"
            }
        }
    }
}
