package com.yaropaul.chatbotaiassistant.domain.repository

import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for chat operations.
 * This belongs to the domain layer and defines the contract.
 * Data layer will provide the implementation.
 */
interface IChatRepository {

    /**
     * Sends a message to the chat API and returns the response.
     * @param message The user's message to send
     * @return Result wrapping the AI response text
     */
    suspend fun sendMessage(message: String): Result<String>

    /**
     * Generates an image based on a text prompt.
     * @param prompt The image generation prompt
     * @return Result wrapping list of image URLs
     */
    suspend fun generateImage(prompt: String): Result<List<String>>

    /**
     * Retrieves all messages from local storage as a Flow for reactive updates.
     * @return Flow of list of ChatMessages
     */
    fun getAllMessagesStream(): Flow<List<ChatMessage>>

    /**
     * Retrieves all messages from local storage (one-time fetch).
     * @return Result wrapping list of ChatMessages
     */
    suspend fun getAllMessages(): Result<List<ChatMessage>>

    /**
     * Saves a message to local storage.
     * @param message The message to save
     * @return Result indicating success or failure
     */
    suspend fun insertMessage(message: ChatMessage): Result<Unit>

    /**
     * Deletes all messages from local storage.
     * @return Result indicating success or failure
     */
    suspend fun deleteAllMessages(): Result<Unit>
}
