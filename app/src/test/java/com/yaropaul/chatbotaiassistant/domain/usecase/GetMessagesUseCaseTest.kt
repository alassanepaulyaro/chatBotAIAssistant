package com.yaropaul.chatbotaiassistant.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Unit tests for GetMessagesUseCase.
 * Tests the reactive retrieval of messages.
 */
class GetMessagesUseCaseTest {

    private lateinit var chatRepository: IChatRepository
    private lateinit var getMessagesUseCase: GetMessagesUseCase

    @Before
    fun setup() {
        chatRepository = mockk()
        getMessagesUseCase = GetMessagesUseCase(chatRepository)
    }

    @Test
    fun `invoke returns flow of messages from repository`() = runTest {
        // Given
        val messages = listOf(
            ChatMessage(
                id = "1",
                text = "Hello",
                senderId = "user1",
                senderName = "User",
                senderAvatar = "",
                timestamp = Date(),
                isFromCurrentUser = true
            ),
            ChatMessage(
                id = "2",
                text = "Hi there!",
                senderId = "ai",
                senderName = "AI",
                senderAvatar = "",
                timestamp = Date(),
                isFromCurrentUser = false
            )
        )

        every {
            chatRepository.getAllMessagesStream()
        } returns flowOf(messages)

        // When & Then
        getMessagesUseCase().test {
            val emittedMessages = awaitItem()
            assertThat(emittedMessages).hasSize(2)
            assertThat(emittedMessages[0].text).isEqualTo("Hello")
            assertThat(emittedMessages[1].text).isEqualTo("Hi there!")
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits empty list when no messages exist`() = runTest {
        // Given
        every {
            chatRepository.getAllMessagesStream()
        } returns flowOf(emptyList())

        // When & Then
        getMessagesUseCase().test {
            val emittedMessages = awaitItem()
            assertThat(emittedMessages).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits multiple updates when messages change`() = runTest {
        // Given
        val initialMessages = listOf(
            ChatMessage(
                id = "1",
                text = "First",
                senderId = "user",
                senderName = "User",
                senderAvatar = "",
                timestamp = Date(),
                isFromCurrentUser = true
            )
        )

        val updatedMessages = initialMessages + ChatMessage(
            id = "2",
            text = "Second",
            senderId = "user",
            senderName = "User",
            senderAvatar = "",
            timestamp = Date(),
            isFromCurrentUser = true
        )

        every {
            chatRepository.getAllMessagesStream()
        } returns flowOf(initialMessages, updatedMessages)

        // When & Then
        getMessagesUseCase().test {
            // First emission
            val first = awaitItem()
            assertThat(first).hasSize(1)
            assertThat(first[0].text).isEqualTo("First")

            // Second emission
            val second = awaitItem()
            assertThat(second).hasSize(2)
            assertThat(second[1].text).isEqualTo("Second")

            awaitComplete()
        }
    }
}
