package com.yaropaul.chatbotaiassistant.presentation.chat

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.domain.usecase.*
import com.yaropaul.chatbotaiassistant.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Unit tests for ChatViewModelRefactored.
 * Tests ViewModel logic without Android dependencies.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelRefactoredTest {

    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var generateImageUseCase: GenerateImageUseCase
    private lateinit var getMessagesUseCase: GetMessagesUseCase
    private lateinit var clearMessagesUseCase: ClearMessagesUseCase
    private lateinit var observeConnectivityUseCase: ObserveConnectivityUseCase

    private lateinit var viewModel: ChatViewModelRefactored

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        sendMessageUseCase = mockk()
        generateImageUseCase = mockk()
        getMessagesUseCase = mockk()
        clearMessagesUseCase = mockk()
        observeConnectivityUseCase = mockk()

        // Default mocks
        every { getMessagesUseCase() } returns flowOf(emptyList())
        every { observeConnectivityUseCase() } returns flowOf(true)

        viewModel = ChatViewModelRefactored(
            sendMessageUseCase,
            generateImageUseCase,
            getMessagesUseCase,
            clearMessagesUseCase,
            observeConnectivityUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.messages).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isConnected).isTrue()
            assertThat(state.isTtsEnabled).isFalse()
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun `sendMessage with text sends chat message`() = runTest {
        // Given
        val userInput = "Hello"
        val aiMessage = ChatMessage(
            id = "2",
            text = "Hi!",
            senderId = "2",
            senderName = "AI",
            senderAvatar = "",
            timestamp = Date(),
            isFromCurrentUser = false
        )

        coEvery {
            sendMessageUseCase(any(), any(), any(), any(), any())
        } returns Result.Success(aiMessage)

        // When
        viewModel.sendMessage(userInput)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            sendMessageUseCase(
                userMessage = userInput,
                currentUserId = "1",
                currentUserName = "Yaropaul",
                aiUserId = "2",
                aiUserName = "ChatGPT"
            )
        }
    }

    @Test
    fun `sendMessage with blank input does nothing`() = runTest {
        // When
        viewModel.sendMessage("  ")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) {
            sendMessageUseCase(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `sendMessage with image keyword calls generateImage`() = runTest {
        // Given
        val prompt = "generate image of cat"
        val imageMessage = ChatMessage(
            id = "1",
            text = "Generated image",
            senderId = "2",
            senderName = "AI",
            senderAvatar = "",
            timestamp = Date(),
            imageUrl = "https://example.com/cat.png",
            isFromCurrentUser = false
        )

        coEvery {
            generateImageUseCase(any(), any(), any(), any(), any())
        } returns Result.Success(listOf(imageMessage))

        // When
        viewModel.sendMessage(prompt)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            generateImageUseCase(
                prompt = prompt,
                currentUserId = "1",
                currentUserName = "Yaropaul",
                aiUserId = "2",
                aiUserName = "ChatGPT"
            )
        }

        coVerify(exactly = 0) {
            sendMessageUseCase(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `sendMessage sets loading state`() = runTest {
        // Given
        val aiMessage = ChatMessage(
            id = "1",
            text = "Response",
            senderId = "2",
            senderName = "AI",
            senderAvatar = "",
            timestamp = Date(),
            isFromCurrentUser = false
        )

        coEvery {
            sendMessageUseCase(any(), any(), any(), any(), any())
        } coAnswers {
            // Simulate delay
            kotlinx.coroutines.delay(100)
            Result.Success(aiMessage)
        }

        // When
        viewModel.sendMessage("Test")

        // Then - loading should be true initially
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse() // Initial state

            viewModel.sendMessage("Test2")
            testDispatcher.scheduler.advanceTimeBy(50)

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            testDispatcher.scheduler.advanceUntilIdle()

            val finalState = awaitItem()
            assertThat(finalState.isLoading).isFalse()
        }
    }

    @Test
    fun `sendMessage error updates error state and emits event`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery {
            sendMessageUseCase(any(), any(), any(), any(), any())
        } returns Result.Error(Exception(), errorMessage)

        // Start collecting the state Flow to activate it
        val states = mutableListOf<ChatUiState>()
        val collectJob = this.launch(UnconfinedTestDispatcher(testDispatcher.scheduler)) {
            viewModel.uiState.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.sendMessage("Test")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - verify error state
        val finalState = states.last()
        assertThat(finalState.errorMessage).isEqualTo(errorMessage)
        assertThat(finalState.isLoading).isFalse()

        // Verify error event was sent
        coVerify {
            sendMessageUseCase(any(), any(), any(), any(), any())
        }

        collectJob.cancel()
    }

    @Test
    fun `toggleTts changes TTS state`() = runTest {
        // Given
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.isTtsEnabled).isFalse()

            // When
            viewModel.toggleTts()

            // Then
            val updatedState = awaitItem()
            assertThat(updatedState.isTtsEnabled).isTrue()

            // Toggle again
            viewModel.toggleTts()

            val finalState = awaitItem()
            assertThat(finalState.isTtsEnabled).isFalse()
        }
    }

    @Test
    fun `clearMessages calls usecase and emits event`() = runTest {
        // Given
        coEvery {
            clearMessagesUseCase()
        } returns Result.Success(Unit)

        // When
        viewModel.clearMessages()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) {
            clearMessagesUseCase()
        }

        viewModel.events.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(ChatUiEvent.MessagesCleared::class.java)
        }
    }

    @Test
    fun `clearError clears error message`() = runTest {
        // Given - set an error first
        coEvery {
            sendMessageUseCase(any(), any(), any(), any(), any())
        } returns Result.Error(Exception(), "Error")

        viewModel.sendMessage("Test")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun `messages flow is observed from usecase`() = runTest {
        // Given
        val messages = listOf(
            ChatMessage(
                id = "1",
                text = "Hello",
                senderId = "1",
                senderName = "User",
                senderAvatar = "",
                timestamp = Date(),
                isFromCurrentUser = true
            )
        )

        every { getMessagesUseCase() } returns flowOf(messages)

        // Create new ViewModel to pick up the new flow
        val vm = ChatViewModelRefactored(
            sendMessageUseCase,
            generateImageUseCase,
            getMessagesUseCase,
            clearMessagesUseCase,
            observeConnectivityUseCase
        )

        // Start collecting the state Flow to activate it
        val states = mutableListOf<ChatUiState>()
        val collectJob = this.launch(UnconfinedTestDispatcher(testDispatcher.scheduler)) {
            vm.uiState.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = states.last()
        assertThat(state.messages).hasSize(1)
        assertThat(state.messages[0].text).isEqualTo("Hello")

        collectJob.cancel()
    }

    @Test
    fun `connectivity changes are reflected in state`() = runTest {
        // Given
        every { observeConnectivityUseCase() } returns flowOf(false)

        // Create new ViewModel
        val vm = ChatViewModelRefactored(
            sendMessageUseCase,
            generateImageUseCase,
            getMessagesUseCase,
            clearMessagesUseCase,
            observeConnectivityUseCase
        )

        // Start collecting the state Flow to activate it
        val states = mutableListOf<ChatUiState>()
        val collectJob = this.launch(UnconfinedTestDispatcher(testDispatcher.scheduler)) {
            vm.uiState.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = states.last()
        assertThat(state.isConnected).isFalse()

        collectJob.cancel()
    }

    @Test
    fun `sendMessage when disconnected shows error`() = runTest {
        // Given
        every { observeConnectivityUseCase() } returns flowOf(false)

        val vm = ChatViewModelRefactored(
            sendMessageUseCase,
            generateImageUseCase,
            getMessagesUseCase,
            clearMessagesUseCase,
            observeConnectivityUseCase
        )

        // Start collecting the state Flow to activate it
        val states = mutableListOf<ChatUiState>()
        val collectJob = this.launch(UnconfinedTestDispatcher(testDispatcher.scheduler)) {
            vm.uiState.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // When
        vm.sendMessage("Test")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should not call usecase
        coVerify(exactly = 0) {
            sendMessageUseCase(any(), any(), any(), any(), any())
        }

        // Verify connectivity state is false
        assertThat(states.last().isConnected).isFalse()

        collectJob.cancel()
    }
}
