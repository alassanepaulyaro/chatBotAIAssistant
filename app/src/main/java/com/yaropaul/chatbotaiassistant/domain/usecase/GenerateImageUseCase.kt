package com.yaropaul.chatbotaiassistant.domain.usecase

import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import com.yaropaul.chatbotaiassistant.domain.util.Result
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for generating images from text prompts.
 * Encapsulates the business logic: save user message, generate images, save image messages.
 *
 * This follows the Single Responsibility Principle and makes testing easier.
 */
class GenerateImageUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    companion object {
        // Business rule: Keywords that trigger image generation
        val IMAGE_GENERATION_KEYWORDS = listOf(
            "generate image",
            "create image",
            "draw image",
            "make image",
            "generate picture",
            "create picture"
        )

        /**
         * Checks if a message should trigger image generation.
         */
        fun shouldGenerateImage(message: String): Boolean {
            return IMAGE_GENERATION_KEYWORDS.any {
                message.startsWith(it, ignoreCase = true)
            }
        }
    }

    /**
     * Executes the use case to generate images from a text prompt.
     *
     * @param prompt The image generation prompt
     * @param currentUserId The ID of the current user
     * @param currentUserName The name of the current user
     * @param aiUserId The ID of the AI assistant
     * @param aiUserName The name of the AI assistant
     * @return Result<List<ChatMessage>> containing messages for each generated image
     */
    suspend operator fun invoke(
        prompt: String,
        currentUserId: String,
        currentUserName: String,
        aiUserId: String,
        aiUserName: String
    ): Result<List<ChatMessage>> {
        // 1. Create and save user's prompt message
        val userPromptMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = prompt,
            senderId = currentUserId,
            senderName = currentUserName,
            senderAvatar = "",
            timestamp = Date(),
            isFromCurrentUser = true
        )

        // Save user prompt to local storage
        val saveUserResult = chatRepository.insertMessage(userPromptMessage)
        if (saveUserResult is Result.Error) {
            return Result.Error(
                saveUserResult.exception,
                "Failed to save user prompt"
            )
        }

        // 2. Generate images from API
        return when (val apiResult = chatRepository.generateImage(prompt)) {
            is Result.Success -> {
                val imageUrls = apiResult.data
                val imageMessages = mutableListOf<ChatMessage>()

                // 3. Create and save a message for each generated image
                imageUrls.forEach { imageUrl ->
                    val imageMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = "Generated image",
                        senderId = aiUserId,
                        senderName = aiUserName,
                        senderAvatar = "",
                        timestamp = Date(),
                        imageUrl = imageUrl,
                        isFromCurrentUser = false
                    )

                    // Save each image message
                    when (val saveResult = chatRepository.insertMessage(imageMessage)) {
                        is Result.Success -> imageMessages.add(imageMessage)
                        is Result.Error -> {
                            return Result.Error(
                                saveResult.exception,
                                "Failed to save image message"
                            )
                        }
                        is Result.Loading -> {} // Continue
                    }
                }

                Result.Success(imageMessages)
            }
            is Result.Error -> Result.Error(
                apiResult.exception,
                apiResult.message ?: "Failed to generate images"
            )
            is Result.Loading -> Result.Loading
        }
    }
}
