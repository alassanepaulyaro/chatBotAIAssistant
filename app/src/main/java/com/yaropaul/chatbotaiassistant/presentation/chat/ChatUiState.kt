package com.yaropaul.chatbotaiassistant.presentation.chat

import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage

/**
 * Represents the complete UI state for the chat screen.
 * Using a single data class for UI state is a best practice in MVVM.
 *
 * Benefits:
 * - Single source of truth for UI state
 * - Immutable state prevents accidental modifications
 * - Easy to test and debug
 * - Clear separation of concerns
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isConnected: Boolean = true,
    val isTtsEnabled: Boolean = false,
    val errorMessage: String? = null,
    val currentUserId: String = "1",
    val currentUserName: String = "Yaropaul",
    val aiUserId: String = "2",
    val aiUserName: String = "ChatGPT"
)

/**
 * Represents one-time events that should not persist in the UI state.
 * Examples: showing a snackbar, navigation events, etc.
 *
 * Using sealed class ensures type safety and exhaustive when statements.
 */
sealed class ChatUiEvent {
    data class ShowError(val message: String) : ChatUiEvent()
    data class SpeakText(val text: String) : ChatUiEvent()
    data object MessageSent : ChatUiEvent()
    data object MessagesCleared : ChatUiEvent()
}
