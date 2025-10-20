package com.yaropaul.chatbotaiassistant

import android.Manifest
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.yaropaul.chatbotaiassistant.presentation.chat.ChatUiEvent
import com.yaropaul.chatbotaiassistant.presentation.chat.ChatViewModelRefactored
import com.yaropaul.chatbotaiassistant.ui.chat.ChatScreen
import com.yaropaul.chatbotaiassistant.ui.theme.ChatBotAIAssistantTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

/**
 * MainActivity using Clean Architecture with ChatViewModelRefactored.
 *
 * Key improvements:
 * - Uses ChatViewModelRefactored (Clean Architecture)
 * - Single UI state observation
 * - Event-based architecture for one-time events
 * - TTS managed in Activity (not in ViewModel)
 * - Proper lifecycle management
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private val chatViewModel: ChatViewModelRefactored by viewModels()
    private var textToSpeech: TextToSpeech? = null
    private var ttsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(applicationContext, this)

        // Request microphone permission
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Permission micro refusée", Toast.LENGTH_SHORT).show()
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        enableEdgeToEdge()
        setContent {
            ChatBotAIAssistantTheme {
                val uiState by chatViewModel.uiState.collectAsState()

                // Handle TTS events
                LaunchedEffect(Unit) {
                    chatViewModel.events.collect { event ->
                        when (event) {
                            is ChatUiEvent.SpeakText -> {
                                if (ttsInitialized && uiState.isTtsEnabled) {
                                    textToSpeech?.speak(
                                        event.text,
                                        TextToSpeech.QUEUE_ADD,
                                        null,
                                        null
                                    )
                                }
                            }
                            else -> {
                                // Other events handled by ChatScreen
                            }
                        }
                    }
                }

                ChatScreen(
                    uiState = uiState,
                    events = chatViewModel.events,
                    onSendMessage = { userInput ->
                        chatViewModel.sendMessage(userInput)
                    },
                    onClearMessages = {
                        chatViewModel.clearMessages()
                    },
                    onToggleTTS = {
                        chatViewModel.toggleTts()
                    },
                    onEvent = { event ->
                        // Handle events that need Activity context
                        when (event) {
                            is ChatUiEvent.SpeakText -> {
                                if (ttsInitialized && uiState.isTtsEnabled) {
                                    textToSpeech?.speak(
                                        event.text,
                                        TextToSpeech.QUEUE_ADD,
                                        null,
                                        null
                                    )
                                }
                            }
                            else -> {
                                // Other events
                            }
                        }
                    }
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.getDefault()
            ttsInitialized = true
        } else {
            ttsInitialized = false
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
