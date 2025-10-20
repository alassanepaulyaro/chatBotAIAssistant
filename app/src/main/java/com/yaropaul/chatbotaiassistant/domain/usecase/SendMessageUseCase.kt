package com.yaropaul.chatbotaiassistant.domain.usecase

import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import com.yaropaul.chatbotaiassistant.domain.util.Result
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for sending a chat message.
 * Encapsulates the business logic: save user message, send to API, save response.
 *
 * This follows the Single Responsibility Principle and makes testing easier.
 */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    /**
     * Executes the use case to send a message and save both user message and AI response.
     *
     * @param userMessage The text message from the user
     * @param currentUserId The ID of the current user
     * @param currentUserName The name of the current user
     * @param aiUserId The ID of the AI assistant
     * @param aiUserName The name of the AI assistant
     * @return Result<ChatMessage> containing the AI's response message
     */
    suspend operator fun invoke(
        userMessage: String,
        currentUserId: String,
        currentUserName: String,
        aiUserId: String,
        aiUserName: String
    ): Result<ChatMessage> {
        // 1. Create and save user's message
        val userChatMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = userMessage,
            senderId = currentUserId,
            senderName = currentUserName,
            senderAvatar = "",
            timestamp = Date(),
            isFromCurrentUser = true
        )

        // Save user message to local storage
        val saveUserResult = chatRepository.insertMessage(userChatMessage)
        if (saveUserResult is Result.Error) {
            return Result.Error(
                saveUserResult.exception,
                "Failed to save user message"
            )
        }

        // 2. Send message to API
        return when (val apiResult = chatRepository.sendMessage(userMessage)) {
            is Result.Success -> {
                // 3. Create and save AI response message
                val aiChatMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = apiResult.data,
                    senderId = aiUserId,
                    senderName = aiUserName,
                    senderAvatar = "",
                    timestamp = Date(),
                    isFromCurrentUser = false
                )

                // Save AI response to local storage
                when (val saveAiResult = chatRepository.insertMessage(aiChatMessage)) {
                    is Result.Success -> Result.Success(aiChatMessage)
                    is Result.Error -> Result.Error(
                        saveAiResult.exception,
                        "Failed to save AI response"
                    )
                    is Result.Loading -> Result.Loading
                }
            }
            is Result.Error -> Result.Error(
                apiResult.exception,
                apiResult.message ?: "Failed to send message"
            )
            is Result.Loading -> Result.Loading
        }
    }
}
