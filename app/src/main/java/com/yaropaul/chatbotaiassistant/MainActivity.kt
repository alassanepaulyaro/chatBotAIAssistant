package com.yaropaul.chatbotaiassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yaropaul.chatbotaiassistant.ui.chat.ChatScreen
import com.yaropaul.chatbotaiassistant.ui.theme.ChatBotAIAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatBotAIAssistantTheme {
                ChatScreen()
            }
        }
    }
}
