package com.yaropaul.chatbotaiassistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onSurface = Color.Black
)

@Composable
fun ChatBotAIAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // remove dynamic color for now
       /* dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}