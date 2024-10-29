package com.yaropaul.chatbotaiassistant.data.model

data class ChatRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<MessageRequest>
)

data class MessageRequest(
    val role: String,
    val content: String
)
