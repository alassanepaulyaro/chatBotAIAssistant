package com.yaropaul.chatbotaiassistant.data.model

data class ImageResponse(
    val created: Long,
    val data: List<ImageData>
)

data class ImageData(
    val url: String
)