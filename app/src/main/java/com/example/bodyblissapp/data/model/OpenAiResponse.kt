package com.example.bodyblissapp.data.model

data class OpenAiResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageContent
)

data class MessageContent(
    val content: String
)
