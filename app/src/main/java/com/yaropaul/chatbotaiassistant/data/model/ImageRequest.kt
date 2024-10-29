package com.yaropaul.chatbotaiassistant.data.model


data class ImageRequest(
    val prompt: String,
    val n: Int = 2,
    val size: String = "1024x1024"
)