package com.yaropaul.chatbotaiassistant.data.repository

import com.yaropaul.chatbotaiassistant.data.apiRemote.ApiService
import com.yaropaul.chatbotaiassistant.data.local.MessageDao
import com.yaropaul.chatbotaiassistant.data.mapper.toDomainList
import com.yaropaul.chatbotaiassistant.data.mapper.toEntity
import com.yaropaul.chatbotaiassistant.data.model.ChatRequest
import com.yaropaul.chatbotaiassistant.data.model.ImageRequest
import com.yaropaul.chatbotaiassistant.data.model.MessageRequest
import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import com.yaropaul.chatbotaiassistant.domain.util.Result
import com.yaropaul.chatbotaiassistant.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of IChatRepository interface.
 * This belongs to the data layer and handles all data operations.
 * It coordinates between remote API and local database.
 *
 * Benefits:
 * - Implements interface from domain layer (dependency inversion)
 * - Encapsulates data source details
 * - Easy to test with mocks
 * - Can be swapped with different implementations
 */
class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val messageDao: MessageDao,
    private val currentUserId: String = "1" // Injected via DI in production
) : IChatRepository {

    /**
     * Sends a message to the chat API.
     * Wraps response in Result for error handling.
     */
    override suspend fun sendMessage(message: String): Result<String> {
        return try {
            val request = ChatRequest(
                messages = listOf(
                    MessageRequest(role = "user", content = message)
                )
            )
            val response = apiService.sendMessage(
                request = request,
                authToken = "Bearer ${Constants.API_KEY}"
            )

            // Extract the response text from the API response
            val responseText = response.choices.firstOrNull()?.message?.content?.trim()

            if (responseText.isNullOrBlank()) {
                Result.Error(
                    Exception("Empty response from API"),
                    "No response received"
                )
            } else {
                Result.Success(responseText)
            }
        } catch (e: Exception) {
            Result.Error(e, "Failed to send message: ${e.localizedMessage}")
        }
    }

    /**
     * Generates images from a text prompt.
     * Wraps response in Result for error handling.
     */
    override suspend fun generateImage(prompt: String): Result<List<String>> {
        return try {
            val request = ImageRequest(prompt = prompt)
            val response = apiService.generateImage(
                request = request,
                authToken = "Bearer ${Constants.API_KEY}"
            )

            // Extract image URLs from the API response
            val imageUrls = response.data.mapNotNull { it.url }

            if (imageUrls.isEmpty()) {
                Result.Error(
                    Exception("No images generated"),
                    "Failed to generate images"
                )
            } else {
                Result.Success(imageUrls)
            }
        } catch (e: Exception) {
            Result.Error(e, "Failed to generate images: ${e.localizedMessage}")
        }
    }

    /**
     * Retrieves all messages from local database as a Flow.
     * This enables reactive updates - UI automatically refreshes when data changes.
     */
    override fun getAllMessagesStream(): Flow<List<ChatMessage>> {
        return messageDao.getAllMessagesFlow().map { messages ->
            messages.toDomainList(currentUserId)
        }
    }

    /**
     * Retrieves all messages from local database (one-time fetch).
     */
    override suspend fun getAllMessages(): Result<List<ChatMessage>> {
        return try {
            val messages = messageDao.getAllMessages()
            Result.Success(messages.toDomainList(currentUserId))
        } catch (e: Exception) {
            Result.Error(e, "Failed to retrieve messages")
        }
    }

    /**
     * Inserts a message into local database.
     */
    override suspend fun insertMessage(message: ChatMessage): Result<Unit> {
        return try {
            messageDao.insertMessage(message.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Failed to insert message")
        }
    }

    /**
     * Deletes all messages from local database.
     */
    override suspend fun deleteAllMessages(): Result<Unit> {
        return try {
            messageDao.deleteAllMessages()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Failed to delete messages")
        }
    }
}
