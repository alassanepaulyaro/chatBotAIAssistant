package com.yaropaul.chatbotaiassistant.domain.usecase

import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving messages.
 * Encapsulates the business logic for fetching messages from local storage.
 *
 * This follows the Single Responsibility Principle and makes testing easier.
 */
class GetMessagesUseCase @Inject constructor(
    private val chatRepository: IChatRepository
) {
    /**
     * Retrieves all messages as a Flow for reactive updates.
     * The UI will automatically update when messages change.
     *
     * @return Flow<List<ChatMessage>> emitting list of messages
     */
    operator fun invoke(): Flow<List<ChatMessage>> {
        return chatRepository.getAllMessagesStream()
    }
}
