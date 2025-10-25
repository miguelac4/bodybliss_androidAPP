package com.example.bodyblissapp.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.network.RetrofitInstance
import com.example.bodyblissapp.util.SessionManager
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    var name by mutableStateOf("")
    var gender by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var loading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun register(
        context: Context,
        onSuccess: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        loading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.loginApi.registerUser(
                    name = name,
                    email = email,
                    password = password,
                    gender = gender
                )

                if (response.success) {
                    val sessionManager = SessionManager(context)
                    sessionManager.saveUserSession(
                        userId = response.user_id ?: -1,
                        name = name,
                        email = email,
                        role = "client"
                    )
                    onSuccess(name, email)
                } else {
                    errorMessage = response.error ?: "Erro desconhecido."
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
