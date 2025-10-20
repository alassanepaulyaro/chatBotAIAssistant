package com.yaropaul.chatbotaiassistant.domain.model

import java.util.Date

/**
 * Domain model representing a chat message.
 * This is a pure Kotlin class with no Android dependencies.
 */
data class ChatMessage(
    val id: String,
    val text: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val timestamp: Date,
    val imageUrl: String? = null,
    val isFromCurrentUser: Boolean = false
)
