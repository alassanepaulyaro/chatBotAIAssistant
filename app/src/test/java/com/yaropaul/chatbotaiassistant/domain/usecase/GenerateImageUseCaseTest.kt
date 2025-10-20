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
 * Unit tests for GenerateImageUseCase.
 * Tests image generation business logic.
 */
class GenerateImageUseCaseTest {

    private lateinit var chatRepository: IChatRepository
    private lateinit var generateImageUseCase: GenerateImageUseCase

    @Before
    fun setup() {
        chatRepository = mockk()
        generateImageUseCase = GenerateImageUseCase(chatRepository)
    }

    @Test
    fun `shouldGenerateImage returns true for valid keywords`() {
        // Given & When & Then
        assertThat(GenerateImageUseCase.shouldGenerateImage("generate image of a cat")).isTrue()
        assertThat(GenerateImageUseCase.shouldGenerateImage("Generate Image of a cat")).isTrue() // Case insensitive
        assertThat(GenerateImageUseCase.shouldGenerateImage("create image of a dog")).isTrue()
        assertThat(GenerateImageUseCase.shouldGenerateImage("draw image of a house")).isTrue()
        assertThat(GenerateImageUseCase.shouldGenerateImage("make image of a tree")).isTrue()
        assertThat(GenerateImageUseCase.shouldGenerateImage("generate picture of sunset")).isTrue()
        assertThat(GenerateImageUseCase.shouldGenerateImage("create picture of mountains")).isTrue()
    }

    @Test
    fun `shouldGenerateImage returns false for invalid keywords`() {
        // Given & When & Then
        assertThat(GenerateImageUseCase.shouldGenerateImage("hello world")).isFalse()
        assertThat(GenerateImageUseCase.shouldGenerateImage("show me an image")).isFalse()
        assertThat(GenerateImageUseCase.shouldGenerateImage("image generator")).isFalse()
        assertThat(GenerateImageUseCase.shouldGenerateImage("I want to generate")).isFalse()
    }

    @Test
    fun `invoke successfully generates images and saves messages`() = runTest {
        // Given
        val prompt = "generate image of a cat"
        val imageUrls = listOf(
            "https://example.com/image1.png",
            "https://example.com/image2.png"
        )

        coEvery {
            chatRepository.insertMessage(any())
        } returns Result.Success(Unit)

        coEvery {
            chatRepository.generateImage(prompt)
        } returns Result.Success(imageUrls)

        // When
        val result = generateImageUseCase(
            prompt = prompt,
            currentUserId = "1",
            currentUserName = "User",
            aiUserId = "2",
            aiUserName = "AI"
        )

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).hasSize(2)
        assertThat(successResult.data[0].imageUrl).isEqualTo(imageUrls[0])
        assertThat(successResult.data[1].imageUrl).isEqualTo(imageUrls[1])

        // Verify user prompt was saved
        coVerify(exactly = 1) {
            chatRepository.insertMessage(match {
                it.text == prompt && it.senderId == "1"
            })
        }

        // Verify image messages were saved (2 images)
        coVerify(exactly = 2) {
            chatRepository.insertMessage(match {
                it.imageUrl != null && it.senderId == "2"
            })
        }
    }

    @Test
    fun `invoke returns error when API fails`() = runTest {
        // Given
        val prompt = "generate image"
        val exception = Exception("API error")

        coEvery {
            chatRepository.insertMessage(any())
        } returns Result.Success(Unit)

        coEvery {
            chatRepository.generateImage(prompt)
        } returns Result.Error(exception, "Failed to generate")

        // When
        val result = generateImageUseCase(
            prompt = prompt,
            currentUserId = "1",
            currentUserName = "User",
            aiUserId = "2",
            aiUserName = "AI"
        )

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.message).contains("Failed to generate")
    }

    @Test
    fun `invoke returns error when saving user prompt fails`() = runTest {
        // Given
        val prompt = "generate image"
        val exception = Exception("Database error")

        coEvery {
            chatRepository.insertMessage(any())
        } returns Result.Error(exception)

        // When
        val result = generateImageUseCase(
            prompt = prompt,
            currentUserId = "1",
            currentUserName = "User",
            aiUserId = "2",
            aiUserName = "AI"
        )

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)

        // Verify API was not called
        coVerify(exactly = 0) {
            chatRepository.generateImage(any())
        }
    }

    @Test
    fun `invoke sets correct message properties`() = runTest {
        // Given
        val prompt = "generate image of sunset"
        val imageUrl = "https://example.com/sunset.png"

        coEvery {
            chatRepository.insertMessage(any())
        } returns Result.Success(Unit)

        coEvery {
            chatRepository.generateImage(prompt)
        } returns Result.Success(listOf(imageUrl))

        // When
        val result = generateImageUseCase(
            prompt = prompt,
            currentUserId = "user123",
            currentUserName = "John",
            aiUserId = "ai456",
            aiUserName = "ChatGPT"
        )

        // Then
        val successResult = result as Result.Success
        val imageMessage = successResult.data[0]

        assertThat(imageMessage.text).isEqualTo("Generated image")
        assertThat(imageMessage.senderId).isEqualTo("ai456")
        assertThat(imageMessage.senderName).isEqualTo("ChatGPT")
        assertThat(imageMessage.imageUrl).isEqualTo(imageUrl)
        assertThat(imageMessage.isFromCurrentUser).isFalse()
    }

    @Test
    fun `invoke returns error when saving image message fails`() = runTest {
        // Given
        val prompt = "generate image"
        val imageUrl = "https://example.com/image.png"

        // First call (user prompt) succeeds, second call (image message) fails
        coEvery {
            chatRepository.insertMessage(match { it.imageUrl == null })
        } returns Result.Success(Unit)

        coEvery {
            chatRepository.insertMessage(match { it.imageUrl != null })
        } returns Result.Error(Exception("Save failed"))

        coEvery {
            chatRepository.generateImage(prompt)
        } returns Result.Success(listOf(imageUrl))

        // When
        val result = generateImageUseCase(
            prompt = prompt,
            currentUserId = "1",
            currentUserName = "User",
            aiUserId = "2",
            aiUserName = "AI"
        )

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.message).contains("Failed to save image message")
    }
}
