package com.yaropaul.chatbotaiassistant.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.yaropaul.chatbotaiassistant.R
import com.yaropaul.chatbotaiassistant.ui.theme.ChatBotAIAssistantTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    // Configure the scroll behavior for the TopAppBar
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // Scaffold to structure the layout
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = Color.White
                ),
                title = { Text("ChatBot AI Assistant") },
                actions = {
                    IconButton(onClick = { /*TODO*/ },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.background, // Set the background color
                            contentColor = Color.White // Set the icon color
                        )) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_voice_over_off),
                            contentDescription = "Mic Off",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
               scrollBehavior = scrollBehavior // Attach scroll behavior to the TopAppBar
            )
        },
        content = { paddingValues ->
            // Main content with background using Surface
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Layout for messages and input bar
                ConstraintLayout(
                    modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background) // Background color for the content
                ) {
                    val (messagesList, inputBar) = createRefs()

                    // Placeholder for the messages list (to be implemented)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .constrainAs(messagesList) {
                                top.linkTo(parent.top)
                                bottom.linkTo(inputBar.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                height = Dimension.fillToConstraints
                            }
                    )
                }
            }
        },
        bottomBar = {
            // Bottom input bar, placed within the main content
            BottomInputBar(
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    )
}

@Composable
fun BottomInputBar(modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf("") }

    ConstraintLayout(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface) // Background color for the content
            .fillMaxWidth()
            .padding(8.dp)
            .navigationBarsPadding() // Add padding to avoid the navigation bar
            .imePadding() // If the keyboard is displayed, take into account the necessary padding
    ) {
        val (inputField, micButton, sendButton) = createRefs()

        // Input Text Field
        BasicTextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(inputField) {
                    start.linkTo(parent.start)
                    end.linkTo(micButton.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }
                .background(Color.White, shape = MaterialTheme.shapes.large)
                .padding(10.dp),
            textStyle = TextStyle(
                fontSize = 24.sp, // Set the desired font size here
                color = Color.Black // Optional: Set text color
            )
        )

        // Mic Button
        IconButton(
            onClick = { /* TODO: Implement speech to text */ },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.background, // Set the background color
                contentColor = Color.White // Set the icon color
            ),
            modifier = Modifier
                .constrainAs(micButton) {
                    start.linkTo(inputField.end, margin = 8.dp)
                    end.linkTo(sendButton.start, margin = 16.dp)
                }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_mic_none),
                contentDescription = "Mic Button"
            )
        }

        // Send Button
        IconButton(
            onClick = { /* TODO: Handle sending message */ },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.background, // Set the background color
                contentColor = Color.White // Set the icon color
            ),
            modifier = Modifier
                .constrainAs(sendButton) {
                    end.linkTo(parent.end)
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send Button"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChatBotAIAssistantTheme {
        ChatScreen()
    }
}
