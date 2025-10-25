package com.example.bodyblissapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bodyblissapp.data.model.Message
import com.example.bodyblissapp.data.model.OpenAiRequest
//import com.example.bodyblissapp.data.model.Content
//import com.example.bodyblissapp.data.model.GeminiRequest
//import com.example.bodyblissapp.data.model.Part
import com.example.bodyblissapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiViewModel : ViewModel() {
    private val _suggestion = MutableStateFlow("")
    val suggestion: StateFlow<String> = _suggestion

    suspend fun requestSuggestion(userInput: String) {
        val prompt = "Sugere-me uma rotina de bem-estar baseada no humor atual: $userInput"

        val request = OpenAiRequest(
            messages = listOf(
                Message(role = "user", content = prompt)
            )
        )

        val response = withContext(Dispatchers.IO) {
            RetrofitInstance.openAiApi.getChatCompletion(request)
        }

        _suggestion.value = response.choices
            .firstOrNull()
            ?.message
            ?.content ?: "Sem resposta disponível."
    }
}

