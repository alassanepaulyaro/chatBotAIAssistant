package com.yaropaul.chatbotaiassistant.data.apiRemote

import com.yaropaul.chatbotaiassistant.data.model.ChatRequest
import com.yaropaul.chatbotaiassistant.data.model.ChatResponse
import com.yaropaul.chatbotaiassistant.data.model.ImageRequest
import com.yaropaul.chatbotaiassistant.data.model.ImageResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("chat/completions")
    suspend fun sendMessage(
        @Body request: ChatRequest,
        @Header("Authorization") authToken: String
    ): ChatResponse

    @POST("images/generations")
    suspend fun generateImage(
        @Body request: ImageRequest,
        @Header("Authorization") authToken: String
    ): ImageResponse
}