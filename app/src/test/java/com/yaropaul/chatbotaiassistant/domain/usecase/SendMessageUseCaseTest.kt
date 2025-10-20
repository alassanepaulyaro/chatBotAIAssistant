package com.yaropaul.chatbotaiassistant.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import com.yaropaul.chatbotaiassistant.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Unit tests for SendMessageUseCase.
 * Tests the business logic of sending messages without Android dependencies.
 */
class SendMessageUseCaseTest {

    private lateinit var chatRepository: IChatRepository
    private lateinit var sendMessageUseCase: SendMessageUseCase

    @Before
    fun setup() {
        chatRepository = mockk()
        sendMessageUseCase = SendMessageUseCase(chatRepository)
    }

    @Test
    fun `invoke with valid message returns success with AI response`() = runTest {
        // Given
        val userMessage = "Hello AI"
        val aiResponse = "Hello! How can I help you?"
        val currentUserId = "1"
        val currentUserName = "User"
        val aiUserId = "2"
        val aiUserName = "AI"

        // Mock repository responses
        coEvery {
            chatRepository.insertMessage(any())
        } returns Result.Success(Unit)

        coEvery {
            chatRepository.sendMessage(userMessage)
        } returns Result.Success(aiResponse)

        // When
        val result = sendMessageUseCase(
            userMessage = userMessage,
            currentUserId = currentUserId,
            currentUserName = currentUserName,
            aiUserId = aiUserId,
            aiUserName = aiUserName
        )

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data.text).isEqualTo(aiResponse)
        assertThat(successResult.data.senderId).isEqualTo(aiUserId)
        assertThat(successResult.data.senderName).isEqualTo(aiUserName)

        // Verify user message was saved
        coVerify(exactly = 1) {
            chatRepository.insertMessage(match {
                it.text == userMessage && it.senderId == currentUserId
            })
        }

        // Verify AI response was saved
        coVerify(exactly = 1) {
            chatRepository.insertMessage(match {
                it.text == aiResponse && it.senderId == aiUserId
            })
        }
    }

    @Test
    fun `invoke returns error when repository sendMessage fails`() = runTest {
        // Given
        val userMessage = "Test message"
        val exception = Exception("Network error")

        coEvery {
            chatRepository.insertMessage(any())
        } returns Result.Success(Unit)

        coEvery {
            chatRepository.sendMessage(userMessage)
        } returns Result.Error(exception, "Failed to send")

        // When
        val result = sendMessageUseCase(
            userMessage = userMessage,
            currentUserId = "1",
            currentUserName = "User",
            aiUserId = "2",
            aiUserName = "AI"
        )

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.message).contains("Failed to send")
    }

    @Test
    fun `invoke returns error when saving user message fails`() = runTest {
        // Given
        val userMessage = "Test message"
        val exception = Exception("Database error")

        coEvery {
            chatRepository.insertMessage(any())
        } returns Result.Error(exception, "Failed to save")

        // When
        val result = sendMessageUseCase(
            userMessage = userMessage,
            currentUserId = "1",
            currentUserName = "User",
            aiUserId = "2",
            aiUserName = "AI"
        )

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.message).contains("Failed to save user message")

        // Verify API was not called
        coVerify(exactly = 0) {
            chatRepository.sendMessage(any())
        }
    }

    @Test
    fun `invoke saves both user and AI messages in correct order`() = runTest {
        // Given
        val userMessage = "Question"
        val aiResponse = "Answer"
        val messages = mutableListOf<ChatMessage>()

        coEvery {
            chatRepository.insertMessage(capture(messages))
        } returns Result.Success(Unit)

        coEvery {
            chatRepository.sendMessage(userMessage)
        } returns Result.Success(aiResponse)

        // When
        sendMessageUseCase(
            userMessage = userMessage,
            currentUserId = "1",
            currentUserName = "User",
            aiUserId = "2",
            aiUserName = "AI"
        )

        // Then
        assertThat(messages).hasSize(2)
        assertThat(messages[0].text).isEqualTo(userMessage) // User message first
        assertThat(messages[1].text).isEqualTo(aiResponse)   // AI response second
    }

    @Test
    fun `invoke sets isFromCurrentUser correctly`() = runTest {
        // Given
        val userMessage = "Test"
        val aiResponse = "Response"

        coEvery {
            chatRepository.insertMessage(any())
        } returns Result.Success(Unit)

        coEvery {
            chatRepository.sendMessage(any())
        } returns Result.Success(aiResponse)

        // When
        val result = sendMessageUseCase(
            userMessage = userMessage,
            currentUserId = "1",
            currentUserName = "User",
            aiUserId = "2",
            aiUserName = "AI"
        )

        // Then
        val successResult = result as Result.Success
        assertThat(successResult.data.isFromCurrentUser).isFalse() // AI message

        // Verify user message had isFromCurrentUser = true
        coVerify {
            chatRepository.insertMessage(match {
                it.senderId == "1" && it.isFromCurrentUser == true
            })
        }
    }
}
