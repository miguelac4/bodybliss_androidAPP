package com.example.bodyblissapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.model.CheckoutRequest
import com.example.bodyblissapp.data.model.CheckoutResponse
import com.example.bodyblissapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {

    private val _paymentStatus = MutableStateFlow<String?>(null)
    val paymentStatus: StateFlow<String?> = _paymentStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun submitPayment(context: Context, request: CheckoutRequest) {
        Log.d("CheckoutSubmit", "submitPayment() called")
        viewModelScope.launch {
            Log.d("CheckoutSubmit", "Launching API request with: $request")
            _isLoading.value = true

            try {
                val cleanedCardNumber = request.number.replace(" ", "")
                val formattedExpiry = request.expiry.take(2) + "/" + request.expiry.takeLast(2)

                val response: CheckoutResponse = RetrofitInstance.checkoutService.checkout(
                    userId = request.userId,
                    cardNumber = cleanedCardNumber,
                    expiry = formattedExpiry,
                    cvc = request.cvc,
                    cardName = request.name,
                    lang = request.lang
                )

                Log.d("CheckoutSubmit", "API response: $response")

                _paymentStatus.value = if (response.success == true) {
                    Log.d("CheckoutSubmit", "Payment succeeded")
                    "success"
                } else {
                    Log.d("CheckoutSubmit", "Payment failed: ${response.error}")
                    response.error ?: response.message ?: "Error"
                }

            } catch (e: Exception) {
                Log.e("CheckoutError", "Payment failed", e)
                _paymentStatus.value = "Erro: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearStatus() {
        _paymentStatus.value = null
    }
}