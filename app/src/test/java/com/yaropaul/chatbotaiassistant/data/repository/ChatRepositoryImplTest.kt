package com.yaropaul.chatbotaiassistant.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.yaropaul.chatbotaiassistant.data.apiRemote.ApiService
import com.yaropaul.chatbotaiassistant.data.local.MessageDao
import com.yaropaul.chatbotaiassistant.data.model.ChatRequest
import com.yaropaul.chatbotaiassistant.data.model.ChatResponse
import com.yaropaul.chatbotaiassistant.data.model.Choice
import com.yaropaul.chatbotaiassistant.data.model.ImageData
import com.yaropaul.chatbotaiassistant.data.model.ImageRequest
import com.yaropaul.chatbotaiassistant.data.model.ImageResponse
import com.yaropaul.chatbotaiassistant.data.model.Message
import com.yaropaul.chatbotaiassistant.data.model.MessageResponse
import com.yaropaul.chatbotaiassistant.data.model.Usage
import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import com.yaropaul.chatbotaiassistant.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Integration tests for ChatRepositoryImpl.
 * Tests the repository with mocked data sources (API and DAO).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var messageDao: MessageDao
    private lateinit var repository: IChatRepository

    private val currentUserId = "1"

    @Before
    fun setup() {
        apiService = mockk()
        messageDao = mockk(relaxed = true)
        repository = ChatRepositoryImpl(
            apiService = apiService,
            messageDao = messageDao,
            currentUserId = currentUserId
        )
    }

    @Test
    fun `sendMessage with successful API response returns success`() = runTest {
        // Given
        val userMessage = "Hello"
        val apiResponseText = "Hi! How can I help?"
        val chatResponse = ChatResponse(
            id = "chat-123",
            `object` = "chat.completion",
            created = System.currentTimeMillis(),
            model = "gpt-3.5-turbo",
            choices = listOf(
                Choice(
                    index = 0,
                    message = MessageResponse(
                        role = "assistant",
                        content = apiResponseText
                    ),
                    finish_reason = "stop"
                )
            ),
            usage = Usage(
                prompt_tokens = 10,
                completion_tokens = 20,
                total_tokens = 30
            )
        )

        coEvery {
            apiService.sendMessage(any<ChatRequest>(), any())
        } returns chatResponse

        // When
        val result = repository.sendMessage(userMessage)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isEqualTo(apiResponseText)

        coVerify {
            apiService.sendMessage(any<ChatRequest>(), any())
        }
    }

    @Test
    fun `sendMessage with empty API response returns error`() = runTest {
        // Given
        val userMessage = "Hello"
        val chatResponse = ChatResponse(
            id = "chat-456",
            `object` = "chat.completion",
            created = System.currentTimeMillis(),
            model = "gpt-3.5-turbo",
            choices = listOf(
                Choice(
                    index = 0,
                    message = MessageResponse(
                        role = "assistant",
                        content = "  " // Blank content
                    ),
                    finish_reason = "stop"
                )
            ),
            usage = Usage(
                prompt_tokens = 10,
                completion_tokens = 0,
                total_tokens = 10
            )
        )

        coEvery {
            apiService.sendMessage(any<ChatRequest>(), any())
        } returns chatResponse

        // When
        val result = repository.sendMessage(userMessage)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.message).contains("No response received")
    }

    @Test
    fun `sendMessage with API exception returns error`() = runTest {
        // Given
        val userMessage = "Hello"
        val exception = Exception("Network error")

        coEvery {
            apiService.sendMessage(any<ChatRequest>(), any())
        } throws exception

        // When
        val result = repository.sendMessage(userMessage)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
        assertThat(errorResult.message).contains("Failed to send message")
    }

    @Test
    fun `generateImage with successful API response returns success`() = runTest {
        // Given
        val prompt = "A cat"
        val imageUrl1 = "https://example.com/cat1.png"
        val imageUrl2 = "https://example.com/cat2.png"
        val imageResponse = ImageResponse(
            created = System.currentTimeMillis(),
            data = listOf(
                ImageData(url = imageUrl1),
                ImageData(url = imageUrl2)
            )
        )

        coEvery {
            apiService.generateImage(any<ImageRequest>(), any())
        } returns imageResponse

        // When
        val result = repository.generateImage(prompt)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).hasSize(2)
        assertThat(successResult.data).containsExactly(imageUrl1, imageUrl2)
    }

    @Test
    fun `generateImage with empty response returns error`() = runTest {
        // Given
        val prompt = "A cat"
        val imageResponse = ImageResponse(
            created = System.currentTimeMillis(),
            data = emptyList()
        )

        coEvery {
            apiService.generateImage(any<ImageRequest>(), any())
        } returns imageResponse

        // When
        val result = repository.generateImage(prompt)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.message).contains("Failed to generate images")
    }

    @Test
    fun `generateImage with API exception returns error`() = runTest {
        // Given
        val prompt = "A cat"
        val exception = Exception("API error")

        coEvery {
            apiService.generateImage(any<ImageRequest>(), any())
        } throws exception

        // When
        val result = repository.generateImage(prompt)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
    }

    @Test
    fun `getAllMessagesStream returns flow of messages from DAO`() = runTest {
        // Given
        val message1 = Message(
            id = "1",
            text = "Hello",
            userId = "1",
            userName = "User",
            userAvatar = "",
            date = Date(),
            imageUrl = null
        )
        val message2 = Message(
            id = "2",
            text = "Hi!",
            userId = "2",
            userName = "AI",
            userAvatar = "",
            date = Date(),
            imageUrl = null
        )

        every { messageDao.getAllMessagesFlow() } returns flowOf(listOf(message1, message2))

        // When & Then
        repository.getAllMessagesStream().test {
            val messages = awaitItem()
            assertThat(messages).hasSize(2)
            assertThat(messages[0].text).isEqualTo("Hello")
            assertThat(messages[0].isFromCurrentUser).isTrue()
            assertThat(messages[1].text).isEqualTo("Hi!")
            assertThat(messages[1].isFromCurrentUser).isFalse()
            awaitComplete()
        }
    }

    @Test
    fun `getAllMessages returns all messages from DAO`() = runTest {
        // Given
        val message = Message(
            id = "1",
            text = "Hello",
            userId = "1",
            userName = "User",
            userAvatar = "",
            date = Date(),
            imageUrl = null
        )

        coEvery { messageDao.getAllMessages() } returns listOf(message)

        // When
        val result = repository.getAllMessages()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).hasSize(1)
        assertThat(successResult.data[0].text).isEqualTo("Hello")
    }

    @Test
    fun `getAllMessages with DAO exception returns error`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { messageDao.getAllMessages() } throws exception

        // When
        val result = repository.getAllMessages()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
    }

    @Test
    fun `insertMessage stores message in DAO`() = runTest {
        // Given
        val message = ChatMessage(
            id = "1",
            text = "Hello",
            senderId = "1",
            senderName = "User",
            senderAvatar = "",
            timestamp = Date(),
            isFromCurrentUser = true
        )

        coEvery { messageDao.insertMessage(any()) } returns Unit

        // When
        val result = repository.insertMessage(message)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify { messageDao.insertMessage(any()) }
    }

    @Test
    fun `insertMessage with DAO exception returns error`() = runTest {
        // Given
        val message = ChatMessage(
            id = "1",
            text = "Hello",
            senderId = "1",
            senderName = "User",
            senderAvatar = "",
            timestamp = Date(),
            isFromCurrentUser = true
        )
        val exception = Exception("Insert failed")

        coEvery { messageDao.insertMessage(any()) } throws exception

        // When
        val result = repository.insertMessage(message)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.message).contains("Failed to insert message")
    }

    @Test
    fun `deleteAllMessages clears all messages from DAO`() = runTest {
        // Given
        coEvery { messageDao.deleteAllMessages() } returns Unit

        // When
        val result = repository.deleteAllMessages()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify { messageDao.deleteAllMessages() }
    }

    @Test
    fun `deleteAllMessages with DAO exception returns error`() = runTest {
        // Given
        val exception = Exception("Delete failed")
        coEvery { messageDao.deleteAllMessages() } throws exception

        // When
        val result = repository.deleteAllMessages()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.message).contains("Failed to delete messages")
    }
}
