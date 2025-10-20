package com.yaropaul.chatbotaiassistant.data.mapper

import com.yaropaul.chatbotaiassistant.data.model.Message
import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage

/**
 * Mapper to convert between data layer entities and domain models.
 * This separates concerns and allows the domain layer to remain independent.
 */

/**
 * Converts a Room entity (Message) to a domain model (ChatMessage).
 */
fun Message.toDomain(currentUserId: String): ChatMessage {
    return ChatMessage(
        id = this.id,
        text = this.text,
        senderId = this.userId,
        senderName = this.userName,
        senderAvatar = this.userAvatar,
        timestamp = this.date,
        imageUrl = this.imageUrl,
        isFromCurrentUser = this.userId == currentUserId
    )
}

/**
 * Converts a domain model (ChatMessage) to a Room entity (Message).
 */
fun ChatMessage.toEntity(): Message {
    return Message(
        id = this.id,
        text = this.text,
        userId = this.senderId,
        userName = this.senderName,
        userAvatar = this.senderAvatar,
        date = this.timestamp,
        imageUrl = this.imageUrl
    )
}

/**
 * Converts a list of Room entities to domain models.
 */
fun List<Message>.toDomainList(currentUserId: String): List<ChatMessage> {
    return this.map { it.toDomain(currentUserId) }
}
