package com.yaropaul.chatbotaiassistant.repository

import com.yaropaul.chatbotaiassistant.data.apiRemote.ApiService
import com.yaropaul.chatbotaiassistant.data.local.MessageDao
import com.yaropaul.chatbotaiassistant.data.model.ChatRequest
import com.yaropaul.chatbotaiassistant.data.model.ChatResponse
import com.yaropaul.chatbotaiassistant.data.model.ImageRequest
import com.yaropaul.chatbotaiassistant.data.model.ImageResponse
import com.yaropaul.chatbotaiassistant.data.model.Message
import com.yaropaul.chatbotaiassistant.utils.Constants
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val apiService: ApiService,
    private val messageDao: MessageDao
) {

    suspend fun sendMessage(request: ChatRequest): ChatResponse {
        return apiService.sendMessage(request, "Bearer ${Constants.API_KEY}")
    }

    suspend fun generateImage(request: ImageRequest): ImageResponse {
        return apiService.generateImage(request, "Bearer ${Constants.API_KEY}")
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDao.getAllMessages()
    }

    suspend fun insertMessage(message: Message) {
        messageDao.insertMessage(message)
    }

    suspend fun deleteAllMessages() {
        messageDao.deleteAllMessages()
    }
}
