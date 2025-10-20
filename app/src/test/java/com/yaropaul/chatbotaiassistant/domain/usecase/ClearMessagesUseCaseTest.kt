package com.yaropaul.chatbotaiassistant.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.yaropaul.chatbotaiassistant.domain.repository.IChatRepository
import com.yaropaul.chatbotaiassistant.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ClearMessagesUseCase.
 * Tests message deletion functionality.
 */
class ClearMessagesUseCaseTest {

    private lateinit var chatRepository: IChatRepository
    private lateinit var clearMessagesUseCase: ClearMessagesUseCase

    @Before
    fun setup() {
        chatRepository = mockk()
        clearMessagesUseCase = ClearMessagesUseCase(chatRepository)
    }

    @Test
    fun `invoke successfully clears all messages`() = runTest {
        // Given
        coEvery {
            chatRepository.deleteAllMessages()
        } returns Result.Success(Unit)

        // When
        val result = clearMessagesUseCase()

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        coVerify(exactly = 1) {
            chatRepository.deleteAllMessages()
        }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery {
            chatRepository.deleteAllMessages()
        } returns Result.Error(exception, "Failed to delete")

        // When
        val result = clearMessagesUseCase()

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.message).contains("Failed to delete")
        assertThat(errorResult.exception).isEqualTo(exception)
    }
}
