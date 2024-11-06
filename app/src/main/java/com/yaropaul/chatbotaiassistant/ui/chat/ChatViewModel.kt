package com.yaropaul.chatbotaiassistant.ui.chat

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yaropaul.chatbotaiassistant.data.model.ChatRequest
import com.yaropaul.chatbotaiassistant.data.model.ImageRequest
import com.yaropaul.chatbotaiassistant.data.model.Message
import com.yaropaul.chatbotaiassistant.data.model.MessageRequest
import com.yaropaul.chatbotaiassistant.data.model.User
import com.yaropaul.chatbotaiassistant.repository.ChatRepository
import com.yaropaul.chatbotaiassistant.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    application: Application
) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isTTS = MutableStateFlow(false)
    val isTTS: StateFlow<Boolean> = _isTTS.asStateFlow()
    private var textToSpeech: TextToSpeech

    private val currentUser = User("1", "Yaropaul", "")
    private val chatGptUser = User("2", "ChatGPT", "")

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _errorMessage = MutableStateFlow<Event<String>?>(null)
    val errorMessage: StateFlow<Event<String>?> = _errorMessage.asStateFlow()

    init {
        textToSpeech = TextToSpeech(application.applicationContext, this)
        loadMessages()
        monitorConnectivity(application.applicationContext)
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech.shutdown()
    }

    fun toggleTTS() {
        _isTTS.value = !_isTTS.value
    }

    fun isTTSEnabled(): StateFlow<Boolean> = isTTS

    private fun loadMessages() {
        viewModelScope.launch {
            val savedMessages = repository.getAllMessages()
            _messages.value = savedMessages
        }
    }

    fun clearMessages() {
        viewModelScope.launch {
            repository.deleteAllMessages()
            _messages.value = emptyList()
        }
    }

    fun sendUserMessage(
        userInput: String
    ) {
        val timestamp = getCurrentTimestamp()
        val message = Message(
            id = UUID.randomUUID().toString(),
            text = userInput,
            userId = currentUser.id,
            userName = currentUser.name,
            userAvatar = currentUser.avatar,
            date = timestamp,
            imageUrl = null
        )
        addMessage(message)
        handleUserInput(userInput)
        if (_isTTS.value) {
            textToSpeech.speak(userInput, TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    private fun addMessage(message: Message) {
        viewModelScope.launch {
            repository.insertMessage(message)
            _messages.value = _messages.value + message
        }
    }

    private fun handleUserInput(userInput: String) {
        val keywords = listOf("generate image", "create image", "draw image")
        if (keywords.any { userInput.startsWith(it, ignoreCase = true) }) {
            generateImage(userInput)
        } else {
            sendMessage(userInput)
        }
    }

    private fun sendMessage(userInput: String) {
        viewModelScope.launch {
            if (_isConnected.value) {
                try {
                    val request = ChatRequest(
                        messages = listOf(MessageRequest(role = "user", content = userInput))
                    )
                    val response = repository.sendMessage(request)
                    val resultText = response.choices[0].message.content.trim()
                    val timestamp = getCurrentTimestamp()
                    val message = Message(
                        id = UUID.randomUUID().toString(),
                        text = resultText,
                        userId = chatGptUser.id,
                        userName = chatGptUser.name,
                        userAvatar = chatGptUser.avatar,
                        date = timestamp,
                        imageUrl = null
                    )
                    addMessage(message)
                    if (_isTTS.value) {
                        textToSpeech.speak(resultText, TextToSpeech.QUEUE_ADD, null, null)
                    }
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Erreur lors de l'appel API : ${e.localizedMessage}", e)
                    _errorMessage.value = Event("Une erreur est survenue")
                }
            } else {
                _errorMessage.value = Event("Pas de connexion Internet")
            }
        }
    }

    private fun generateImage(userInput: String) {
        viewModelScope.launch {
            if (_isConnected.value) {
                try {
                    val request = ImageRequest(prompt = userInput)
                    val response = repository.generateImage(request)
                    val timestamp = getCurrentTimestamp()
                    response.data.forEach { imageData ->
                        val message = Message(
                            id = UUID.randomUUID().toString(),
                            text = "image",
                            userId = chatGptUser.id,
                            userName = chatGptUser.name,
                            userAvatar = chatGptUser.avatar,
                            date = timestamp,
                            imageUrl = imageData.url
                        )
                        addMessage(message)
                    }
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Erreur lors de l'appel API : ${e.localizedMessage}", e)
                    _errorMessage.value = Event("Une erreur est survenue")
                }
            } else {
                _errorMessage.value = Event("Pas de connexion Internet")
            }
        }
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.ERROR) {
            textToSpeech.language = Locale.getDefault()
        }
    }

    private fun monitorConnectivity(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                _isConnected.value = true
            }

            override fun onLost(network: android.net.Network) {
                _isConnected.value = false
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    private fun getCurrentTimestamp(): Date {
        return Date()
    }
}