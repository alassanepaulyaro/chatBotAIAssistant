package com.yaropaul.chatbotaiassistant.domain.usecase

import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import com.yaropaul.chatbotaiassistant.domain.util.Result
import javax.inject.Inject

/**
 * Use case for clearing all messages.
 * Encapsulates the business logic for deleting all chat history.
 *
 * This follows the Single Responsibility Principle and makes testing easier.
 */
class ClearMessagesUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    /**
     * Clears all messages from local storage.
     *
     * @return Result<Unit> indicating success or failure
     */
    suspend operator fun invoke(): Result<Unit> {
        return chatRepository.deleteAllMessages()
    }
}
