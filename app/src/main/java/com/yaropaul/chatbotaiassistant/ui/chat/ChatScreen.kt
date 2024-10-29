package com.yaropaul.chatbotaiassistant.ui.chat

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yaropaul.chatbotaiassistant.R
import com.yaropaul.chatbotaiassistant.data.model.Message
import com.yaropaul.chatbotaiassistant.data.model.User
import com.yaropaul.chatbotaiassistant.utils.Event
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onClearMessages: () -> Unit,
    onToggleTTS: () -> Unit,
    isTTSEnabled: Boolean,
    isListening: Boolean,
    isConnected: Boolean,
    errorMessage: Event<String>?,
    currentUser: User,
    chatGptUser: User
) {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var showNoConnectionDialog by remember { mutableStateOf(false) }

    // Managing microphone permissions
    val microphonePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )
    LaunchedEffect(Unit) {
        microphonePermission.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    // Managing the display of error messages
    var showError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    errorMessage?.getContentIfNotHandled()?.let { message ->
        showError = true
        errorText = message
    }

    // Observe changes to isConnected to show the dialog
    LaunchedEffect(isConnected) {
        if (!isConnected) {
            showNoConnectionDialog = true
        }
    }

    // Configures the scrolling behavior for the TopAppBar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    ),
                    title = { Text("ChatBot AI Assistant") },
                    actions = {
                        IconButton(onClick = { showDeleteConfirmationDialog = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_clear_all),
                                contentDescription = "Delete messages"
                            )
                        }
                        // IconButton
                        IconButton(onClick = onToggleTTS) {
                            Icon(
                                imageVector = if (isTTSEnabled)
                                    ImageVector.vectorResource(id = R.drawable.ic_voice_over_on)
                                else
                                    ImageVector.vectorResource(id = R.drawable.ic_voice_over_off),
                                contentDescription = if (isTTSEnabled) "Voice On" else "Voice Off"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        bottomBar = {
            BottomInputBar(
                userInput = userInput,
                onUserInputChange = { userInput = it },
                onSendMessage = {
                    if (userInput.text.isNotBlank()) {
                        onSendMessage(userInput.text)
                        userInput = TextFieldValue("")
                    }
                },
                isListening = isListening
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                MessagesList(
                    messages = messages,
                    currentUser = currentUser,
                    chatGptUser = chatGptUser,
                    modifier = Modifier.fillMaxSize()
                )

                // Showing Snack bar for errors
                if (showError) {
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(2000)
                        showError = false
                    }
                    Snackbar(
                        modifier = Modifier.padding(8.dp),
                        action = {
                            TextButton(onClick = { showError = false }) {
                                Text("Close")
                            }
                        },
                        containerColor = Color.Red
                    ) {
                        Text(text = errorText, color = Color.White)
                    }
                }
            }

            // Delete confirmation dialog
            if (showDeleteConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmationDialog = false },
                    title = { Text("Confirmation") },
                    text = { Text("Are you sure you want to delete everything?") },
                    confirmButton = {
                        TextButton(onClick = {
                            onClearMessages()
                            showDeleteConfirmationDialog = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // No internet connection dialog
            if (showNoConnectionDialog) {
                AlertDialog(
                    onDismissRequest = { showNoConnectionDialog = false },
                    title = { Text("No internet connection") },
                    text = { Text("Please check your internet connection") },
                    confirmButton = {
                        Button(onClick = {
                            showNoConnectionDialog = false
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    )
}


@Composable
fun BottomInputBar(
    userInput: TextFieldValue,
    onUserInputChange: (TextFieldValue) -> Unit,
    onSendMessage: () -> Unit,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = result?.get(0) ?: "No speech detected."
            onUserInputChange(TextFieldValue(recognizedText))
            onSendMessage()
        } else {
            Toast.makeText(context, "[Speech recognition failed.]", Toast.LENGTH_SHORT).show()
        }
    }

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(8.dp)
            .navigationBarsPadding()
            .imePadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = userInput,
            onValueChange = onUserInputChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = { Text("Type your message") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White
            ),
            shape = CircleShape,
            singleLine = true
        )

        // Microphone Button
        IconButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Go on then, say something.")
                launcher.launch(intent)
            },
            modifier = Modifier
                .size(56.dp)
                .padding(start = 8.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_mic_none),
                contentDescription = "Button Mic",
                tint =  MaterialTheme.colorScheme.primary
            )
        }

        // Send Button
        IconButton(
            onClick = onSendMessage,
            modifier = Modifier
                .size(56.dp)
                .padding(start = 8.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_send),
                contentDescription = "Button send",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MessagesList(
    messages: List<Message>,
    currentUser: User,
    chatGptUser: User,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        reverseLayout = true,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(messages.reversed()) { message ->
            MessageItem(
                message = message,
                currentUser = currentUser,
                chatGptUser = chatGptUser
            )
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    currentUser: User,
    chatGptUser: User
) {
    val isCurrentUser = message.userId == currentUser.id
    val isChatGptUser = message.userId == chatGptUser.id
    val backgroundColor = when {
        isCurrentUser -> Color(0xFF2196F3)
        isChatGptUser -> Color(0xFFE0E0E0)
        else -> Color.White
    }
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val paddingStart = if (isCurrentUser) 50.dp else 15.dp
    val paddingEnd = if (isCurrentUser) 15.dp else 50.dp

    // Formatting the date to display "Tuesday - 11:52"
    val sdf = remember { SimpleDateFormat("EEEE - HH:mm", Locale.getDefault()) }
    val formattedDate = remember(message.date) {
        sdf.format(message.date).replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingStart, end = paddingEnd, top = 4.dp, bottom = 4.dp),
        horizontalAlignment = alignment
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = alignment
            ) {
                if (message.imageUrl != null) {
                    // Using Coil to load images
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = "Image of chatbot",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } else {
                    Text(
                        text = message.text,
                        color = textColor
                    )
                }
            }
        }
        Text(
            text = formattedDate,
            color = Color.Gray,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}