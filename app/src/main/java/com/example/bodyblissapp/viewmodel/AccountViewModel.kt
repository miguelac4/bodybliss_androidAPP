package com.example.bodyblissapp.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.model.AvatarResponse
import com.example.bodyblissapp.data.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AccountViewModel : ViewModel() {

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchAvatar(userId: Int) {
        viewModelScope.launch {
            try {
                val response: AvatarResponse = RetrofitInstance.avatarApi.getUserAvatar(userId)
                if (response.success) {
                    _avatarUrl.value = response.image_url
                } else {
                    _error.value = response.error ?: "Erro ao carregar avatar"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Erro de rede"
            }
        }
    }

    fun uploadAvatar(uri: Uri, userId: Int, context: Context) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val file = File(context.cacheDir, "avatar_${userId}.jpg")
        file.outputStream().use { output -> inputStream.copyTo(output) }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("profile_pic", file.name, requestFile)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.avatarApi.uploadAvatar(body, userId)

                Log.d("UPLOAD", "Upload response: $response")
                Log.d("UPLOAD", "Success: ${response.success}")
                Log.d("UPLOAD", "Image URL: ${response.image_url}")
                Log.d("UPLOAD", "Error: ${response.error}")

                if (response.success && response.image_url != null) {
                    _avatarUrl.value = response.image_url
                } else {
                    _error.value = response.error ?: "Upload falhou sem mensagem"
                }

            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("UPLOAD", "HTTP ${e.code()}: $errorBody", e)
            } catch (e: Exception) {
                Log.e("UPLOAD", "Upload failed", e)
            }
        }
    }

    fun removeAvatar(userId: Int, context: Context) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.avatarApi.removeAvatar(userId)
                if (response.success) {
                    _avatarUrl.value = response.image_url // updates UI with default avatar
                } else {
                    _error.value = response.error ?: "Erro ao remover avatar"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Erro de rede"
            }
        }
    }

}
