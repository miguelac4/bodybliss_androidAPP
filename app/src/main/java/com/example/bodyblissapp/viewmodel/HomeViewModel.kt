package com.example.bodyblissapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _averageRating = MutableStateFlow<Float?>(null)
    val averageRating: StateFlow<Float?> = _averageRating

    init {
        fetchAverageRating()
    }

    private fun fetchAverageRating() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getRatingApi.getAverageRating()
                if (response.isSuccessful) {
                    _averageRating.value = response.body()?.avg_rating
                }
            } catch (e: Exception) {
                // Log e tratar erro se quiseres
            }
        }
    }
}
