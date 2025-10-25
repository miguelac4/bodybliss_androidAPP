package com.example.bodyblissapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.network.RetrofitInstance
import com.example.bodyblissapp.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeleteCartViewModel : ViewModel() {

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess: StateFlow<Boolean?> = _deleteSuccess

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError

    fun deleteProductFromCart(context: Context, productId: Int) {
        val userId = SessionManager(context).getUserId()

        viewModelScope.launch {
            _isDeleting.value = true
            try {
                val response = RetrofitInstance.deleteCartService.deleteFromCart(userId, productId)
                if (response.success) {
                    _deleteSuccess.value = true
                    _deleteError.value = null
                } else {
                    _deleteSuccess.value = false
                    _deleteError.value = response.message ?: "Erro ao remover produto"
                }
            } catch (e: Exception) {
                _deleteSuccess.value = false
                _deleteError.value = e.localizedMessage ?: "Erro de rede"
            } finally {
                _isDeleting.value = false
            }
        }
    }
}
