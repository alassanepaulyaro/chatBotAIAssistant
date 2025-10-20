package com.yaropaul.chatbotaiassistant.ui.chat

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yaropaul.chatbotaiassistant.R
import com.yaropaul.chatbotaiassistant.domain.model.ChatMessage
import com.yaropaul.chatbotaiassistant.presentation.chat.ChatUiEvent
import com.yaropaul.chatbotaiassistant.presentation.chat.ChatUiState
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ChatScreen compatible with Clean Architecture.
 * Uses ChatUiState and ChatMessage (domain model) instead of multiple parameters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiState: ChatUiState,
    events: Flow<ChatUiEvent>,
    onSendMessage: (String) -> Unit,
    onClearMessages: () -> Unit,
    onToggleTTS: () -> Unit,
    onEvent: (ChatUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var showNoConnectionDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle events from ViewModel
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ChatUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ChatUiEvent.SpeakText -> {
                    onEvent(event)
                }
                is ChatUiEvent.MessageSent -> {
                    // Message sent successfully
                }
                is ChatUiEvent.MessagesCleared -> {
                    // Messages cleared
                }
            }
        }
    }

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

    // Show connection dialog when disconnected
    LaunchedEffect(uiState.isConnected) {
        if (!uiState.isConnected) {
            showNoConnectionDialog = true
        }
    }

    // Show error message in snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    title = { Text("ChatBot AI") },
                    actions = {
                        IconButton(
                            onClick = { showDeleteConfirmationDialog = true },
                            modifier = Modifier.size(48.dp),
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_clear_all),
                                contentDescription = "Delete messages"
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onToggleTTS,
                            modifier = Modifier.size(48.dp),
                        ) {
                            Icon(
                                imageVector = if (uiState.isTtsEnabled)
                                    ImageVector.vectorResource(id = R.drawable.ic_voice_over_on)
                                else
                                    ImageVector.vectorResource(id = R.drawable.ic_voice_over_off),
                                contentDescription = if (uiState.isTtsEnabled) "Voice On" else "Voice Off"
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
                isLoading = uiState.isLoading
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                MessagesList(
                    messages = uiState.messages,
                    currentUserId = uiState.currentUserId,
                    aiUserId = uiState.aiUserId,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Delete confirmation dialog
            DeleteConfirmationDialog(
                showDeleteConfirmationDialog,
                onClearMessages,
                onDismiss = { showDeleteConfirmationDialog = false }
            )

            // No internet connection dialog
            ConnectionDialog(
                showNoConnectionDialog,
                onDismiss = { showNoConnectionDialog = false }
            )
        }
    )
}

@Composable
private fun ConnectionDialog(showNoConnectionDialog: Boolean, onDismiss: () -> Unit) {
    if (showNoConnectionDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("No internet connection") },
            text = { Text("Please check your internet connection") },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    showDeleteConfirmationDialog: Boolean,
    onClearMessages: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to delete everything?") },
            confirmButton = {
                TextButton(onClick = {
                    onClearMessages()
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BottomInputBar(
    userInput: TextFieldValue,
    onUserInputChange: (TextFieldValue) -> Unit,
    onSendMessage: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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

    ConstraintLayout(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(8.dp)
            .navigationBarsPadding()
            .imePadding()
    ) {
        val (inputField, micButton, sendButton) = createRefs()
        OutlinedTextField(
            value = userInput,
            onValueChange = onUserInputChange,
            modifier = Modifier
                .constrainAs(inputField) {
                    start.linkTo(parent.start)
                    end.linkTo(micButton.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }
                .background(
                    MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.medium
                )
                .height(52.dp),
            placeholder = { Text("Type your message") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            enabled = !isLoading
        )

        // Microphone Button
        IconButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Go on then, say something.")
                launcher.launch(intent)
            },
            modifier = Modifier
                .constrainAs(micButton) {
                    start.linkTo(inputField.end, margin = 8.dp)
                    end.linkTo(sendButton.start, margin = 8.dp)
                }
                .size(48.dp)
                .clipToBounds(),
            enabled = !isLoading
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_mic_none),
                contentDescription = "Button Mic"
            )
        }

        // Send Button
        IconButton(
            onClick = onSendMessage,
            modifier = Modifier
                .size(48.dp)
                .constrainAs(sendButton) {
                    end.linkTo(parent.end)
                },
            interactionSource = remember { MutableInteractionSource() },
            enabled = !isLoading
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_send),
                contentDescription = "Button send"
            )
        }
    }
}

@Composable
fun MessagesList(
    messages: List<ChatMessage>,
    currentUserId: String,
    aiUserId: String,
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
                currentUserId = currentUserId,
                aiUserId = aiUserId
            )
        }
    }
}

@Composable
fun MessageItem(
    message: ChatMessage,
    currentUserId: String,
    aiUserId: String
) {
    val isCurrentUser = message.senderId == currentUserId
    val isAiUser = message.senderId == aiUserId
    val backgroundColor = when {
        isCurrentUser -> Color(0xFF2196F3)
        isAiUser -> Color(0xFFE0E0E0)
        else -> Color.White
    }
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val paddingStart = if (isCurrentUser) 50.dp else 15.dp
    val paddingEnd = if (isCurrentUser) 15.dp else 50.dp

    // Formatting the date to display "Tuesday - 11:52"
    val sdf = remember { SimpleDateFormat("EEEE - HH:mm", Locale.getDefault()) }
    val formattedDate = remember(message.timestamp) {
        sdf.format(message.timestamp).replaceFirstChar { it.uppercase() }
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
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(message.imageUrl)
                            .crossfade(true)
                            .build(),
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
