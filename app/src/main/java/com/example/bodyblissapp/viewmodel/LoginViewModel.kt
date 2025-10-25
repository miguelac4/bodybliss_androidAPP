package com.example.bodyblissapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.network.RetrofitInstance
import com.example.bodyblissapp.util.SessionManager
import kotlinx.coroutines.launch
import android.util.Log


class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var loading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(context : Context, onSuccess: (String, String) -> Unit, onError: (String) -> Unit) {
        loading = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.loginApi.loginUser(email, password)
                if (response.success && response.user_id != null) {
                    val sessionManager = SessionManager(context)
                    response.user_id.let { userId ->
                        sessionManager.saveUserSession(response.user_id,
                            response.name ?: "",
                            response.email ?: "",
                            response.role ?: "")
                    }
                    Log.d("LOGIN_VM", "Sessão guardada com user_id = ${response.user_id}")
                    onSuccess(response.name ?: "", response.email ?: "")
                } else {
                    errorMessage = response.error ?: "Credenciais inválidas."
                    onError(errorMessage!!)
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Erro de rede."
                onError(errorMessage!!)
            } finally {
                loading = false
            }
        }
    }
}
