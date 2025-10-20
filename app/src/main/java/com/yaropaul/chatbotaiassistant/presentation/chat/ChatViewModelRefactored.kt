package com.yaropaul.chatbotaiassistant.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaropaul.chatbotaiassistant.domain.usecase.ClearMessagesUseCase
import com.yaropaul.chatbotaiassistant.domain.usecase.GenerateImageUseCase
import com.yaropaul.chatbotaiassistant.domain.usecase.GetMessagesUseCase
import com.yaropaul.chatbotaiassistant.domain.usecase.ObserveConnectivityUseCase
import com.yaropaul.chatbotaiassistant.domain.usecase.SendMessageUseCase
import com.yaropaul.chatbotaiassistant.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Refactored ViewModel following Clean Architecture and MVVM principles.
 *
 * Key Improvements:
 * 1. Extends ViewModel (not AndroidViewModel) - no Android dependencies
 * 2. Uses UseCases for business logic (Single Responsibility Principle)
 * 3. Single UI state with StateFlow (immutable state management)
 * 4. Proper separation of concerns (no TTS, no ConnectivityManager)
 * 5. Error handling with Result wrapper
 * 6. Event channel for one-time events (prevents event re-emission)
 * 7. Dependency injection via constructor (easy to test)
 * 8. Reactive UI with Flow (no manual state updates)
 *
 * Architecture Flow:
 * UI (Compose) -> ViewModel -> UseCase -> Repository -> Data Source
 *               <-  StateFlow  <-  Result  <-  Flow  <-
 */
@HiltViewModel
class ChatViewModelRefactored @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val generateImageUseCase: GenerateImageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val clearMessagesUseCase: ClearMessagesUseCase,
    private val observeConnectivityUseCase: ObserveConnectivityUseCase
) : ViewModel() {

    // Private mutable state - only ViewModel can modify
    private val _uiState = MutableStateFlow(ChatUiState())

    // Track connectivity separately for internal checks
    private var isConnected = true

    init {
        // Observe connectivity changes
        viewModelScope.launch {
            observeConnectivityUseCase().collect { connected ->
                isConnected = connected
            }
        }
    }

    // Public immutable state - UI observes this
    val uiState: StateFlow<ChatUiState> = combine(
        _uiState,
        getMessagesUseCase(),
        observeConnectivityUseCase()
    ) { state, messages, connected ->
        state.copy(
            messages = messages,
            isConnected = connected
        )
    }.catch { exception ->
        // Handle any errors in the flow
        emit(_uiState.value.copy(errorMessage = exception.localizedMessage))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChatUiState()
    )

    // Channel for one-time events (not state)
    private val _events = Channel<ChatUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /**
     * Handles sending a user message.
     * Determines if it's a chat message or image generation request.
     */
    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        // Check connectivity before proceeding
        if (!isConnected) {
            sendEvent(ChatUiEvent.ShowError("No internet connection"))
            return
        }

        // Determine message type and route accordingly
        if (GenerateImageUseCase.shouldGenerateImage(userInput)) {
            generateImage(userInput)
        } else {
            sendChatMessage(userInput)
        }
    }

    /**
     * Sends a chat message to the API.
     */
    private fun sendChatMessage(message: String) {
        viewModelScope.launch {
            // Set loading state
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Execute use case
            when (val result = sendMessageUseCase(
                userMessage = message,
                currentUserId = _uiState.value.currentUserId,
                currentUserName = _uiState.value.currentUserName,
                aiUserId = _uiState.value.aiUserId,
                aiUserName = _uiState.value.aiUserName
            )) {
                is Result.Success -> {
                    // Success - UI will update via messages flow
                    _uiState.update { it.copy(isLoading = false) }

                    // Emit TTS event if enabled
                    if (_uiState.value.isTtsEnabled) {
                        sendEvent(ChatUiEvent.SpeakText(result.data.text))
                    }

                    sendEvent(ChatUiEvent.MessageSent)
                }
                is Result.Error -> {
                    // Error - show error message
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Failed to send message"
                        )
                    }
                    sendEvent(ChatUiEvent.ShowError(
                        result.message ?: "Failed to send message"
                    ))
                }
                is Result.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    /**
     * Generates images from a text prompt.
     */
    private fun generateImage(prompt: String) {
        viewModelScope.launch {
            // Set loading state
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Execute use case
            when (val result = generateImageUseCase(
                prompt = prompt,
                currentUserId = _uiState.value.currentUserId,
                currentUserName = _uiState.value.currentUserName,
                aiUserId = _uiState.value.aiUserId,
                aiUserName = _uiState.value.aiUserName
            )) {
                is Result.Success -> {
                    // Success - UI will update via messages flow
                    _uiState.update { it.copy(isLoading = false) }
                    sendEvent(ChatUiEvent.MessageSent)
                }
                is Result.Error -> {
                    // Error - show error message
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Failed to generate images"
                        )
                    }
                    sendEvent(ChatUiEvent.ShowError(
                        result.message ?: "Failed to generate images"
                    ))
                }
                is Result.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    /**
     * Clears all messages from the chat.
     */
    fun clearMessages() {
        viewModelScope.launch {
            when (val result = clearMessagesUseCase()) {
                is Result.Success -> {
                    // Success - UI will update via messages flow
                    sendEvent(ChatUiEvent.MessagesCleared)
                }
                is Result.Error -> {
                    sendEvent(ChatUiEvent.ShowError(
                        result.message ?: "Failed to clear messages"
                    ))
                }
                is Result.Loading -> {}
            }
        }
    }

    /**
     * Toggles Text-to-Speech on/off.
     */
    fun toggleTts() {
        _uiState.update { it.copy(isTtsEnabled = !it.isTtsEnabled) }
    }

    /**
     * Clears the current error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Sends a one-time event to the UI.
     */
    private fun sendEvent(event: ChatUiEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }
}

/**
 * COMPARISON WITH OLD VIEWMODEL:
 *
 * OLD (ChatViewModel.kt):
 * - Extends AndroidViewModel (Android dependency)
 * - Implements TextToSpeech.OnInitListener (Android dependency)
 * - Direct ConnectivityManager usage (Android dependency)
 * - Business logic in ViewModel (keywords, message creation)
 * - Multiple StateFlows for different states
 * - Manual state updates
 * - Direct repository calls
 * - NetworkCallback never unregistered (memory leak)
 * - Hard-coded user IDs
 * - Mixed concerns (TTS + Connectivity + Business logic)
 *
 * NEW (ChatViewModelRefactored.kt):
 * - Extends ViewModel (no Android dependencies)
 * - No Android framework dependencies
 * - UseCases handle business logic
 * - Single UI state with StateFlow
 * - Reactive state with combine()
 * - Repository abstraction via interfaces
 * - Proper lifecycle management (no leaks)
 * - Configurable users via UI state
 * - Separation of concerns (each layer has single responsibility)
 * - Testable (can mock UseCases)
 * - Event channel for one-time events
 * - Result wrapper for error handling
 */
