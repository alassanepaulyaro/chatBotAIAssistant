package com.yaropaul.chatbotaiassistant

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.yaropaul.chatbotaiassistant.data.model.User
import com.yaropaul.chatbotaiassistant.ui.chat.ChatScreen
import com.yaropaul.chatbotaiassistant.ui.chat.ChatViewModel
import com.yaropaul.chatbotaiassistant.ui.theme.ChatBotAIAssistantTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val currentUser = User("1", "Yaropaul", "")
    private val chatGptUser = User("2", "ChatGPT", "")
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                val messages = chatViewModel.messages.collectAsState().value
                val isConnected = chatViewModel.isConnected.collectAsState().value
                val errorMessage = chatViewModel.errorMessage.collectAsState().value
                val isVoiceOn = chatViewModel.isTTSEnabled().collectAsState(initial = false).value

                ChatScreen(
                    messages = messages,
                    onSendMessage = { userInput ->
                        chatViewModel.sendUserMessage(userInput)
                    },
                    onClearMessages = {
                        chatViewModel.clearMessages()
                    },
                    onToggleTTS = {
                        chatViewModel.toggleTTS()
                    },
                    isTTSEnabled = isVoiceOn,
                    isConnected = isConnected,
                    errorMessage = errorMessage,
                    currentUser = currentUser,
                    chatGptUser = chatGptUser
                )
            }
        }
    }
}