package com.yaropaul.chatbotaiassistant.data.model

data class ChatRequest(
    val model: String = "gpt-5-nano",
    val messages: List<MessageRequest>
)

data class MessageRequest(
    val role: String,
    val content: String
)
