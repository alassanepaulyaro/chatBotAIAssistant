package com.yaropaul.chatbotaiassistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF19CEF8), // Bright Light Blue
    onPrimary = Color.Black, // Black text on primary
    secondary = Color(0xFF00A7C7), // Soft Cyan Blue
    onSecondary = Color.White, // White text on secondary
    tertiary = Color(0xFF007BB3), // Deeper Blue
    onTertiary = Color.White, // White text on tertiary
    background = Color(0xFF121212), // Deep Black background
    onBackground = Color.White, // White text on background
    surface = Color(0xFF1E1E1E), // Dark Grey surface
    onSurface = Color.White, // White text on surface
    error = Color(0xFFCF6679), // Light Red error
    onError = Color.Black // Black text on error
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF19CEF8), // Bright Light Blue
    onPrimary = Color.White, // White text on primary
    secondary = Color(0xFF00A7C7), // Soft Cyan Blue
    onSecondary = Color.Black, // Black text on secondary
    tertiary = Color(0xFF007BB3), // Deeper Blue
    onTertiary = Color.White, // White text on tertiary
    background = Color(0xFF19CEF8), // White background
    onBackground = Color.Black, // Black text on background
    surface = Color(0xFF00B4EB), // Alice Blue surface
    onSurface = Color.Black, // Black text on surface
    error = Color(0xFFB00020), // Red error
    onError = Color.White // White text on error
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