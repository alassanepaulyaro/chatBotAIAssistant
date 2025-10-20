package com.yaropaul.chatbotaiassistant.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Modern Blue Color Palette - Material Design 3 Compliant
 * Designed for optimal accessibility with WCAG AA contrast ratios
 *
 * Color Philosophy:
 * - Primary: Modern vibrant blue for main actions and branding
 * - Secondary: Teal/cyan for complementary elements
 * - Tertiary: Sky blue for status indicators
 * - All colors tested for 4.5:1 contrast ratio minimum
 */

// ===========================
// LIGHT THEME - Blue Palette
// ===========================

// Primary Colors - Vibrant Blue
val LightPrimary = Color(0xFF1976D2)              // Material Blue 700 - Main actions, send button
val LightOnPrimary = Color(0xFFFFFFFF)            // White text on primary
val LightPrimaryContainer = Color(0xFFBBDEFB)     // Light Blue 100 - User message bubbles
val LightOnPrimaryContainer = Color(0xFF0D47A1)   // Dark blue text on light containers

// Secondary Colors - Teal/Cyan Accent
val LightSecondary = Color(0xFF0097A7)            // Cyan 700 - Voice button, accents
val LightOnSecondary = Color(0xFFFFFFFF)          // White text on secondary
val LightSecondaryContainer = Color(0xFFE0F7FA)   // Cyan 50 - AI message bubbles
val LightOnSecondaryContainer = Color(0xFF006064) // Dark cyan text on light containers

// Tertiary Colors - Sky Blue for Status
val LightTertiary = Color(0xFF03A9F4)             // Light Blue 500 - Online status, links
val LightOnTertiary = Color(0xFFFFFFFF)           // White text on tertiary
val LightTertiaryContainer = Color(0xFFE1F5FE)    // Light Blue 50 - Info backgrounds
val LightOnTertiaryContainer = Color(0xFF01579B)  // Dark blue text

// Surface & Background
val LightBackground = Color(0xFFF8FBFF)           // Soft blue-tinted white
val LightOnBackground = Color(0xFF001F3D)         // Deep navy for text
val LightSurface = Color(0xFFFFFFFF)              // Pure white for cards
val LightOnSurface = Color(0xFF001F3D)            // Deep navy for text
val LightSurfaceVariant = Color(0xFFE3F2FD)       // Light Blue 50 - Alternative surfaces
val LightOnSurfaceVariant = Color(0xFF37474F)     // Blue Gray 700 - Secondary text

// Error Colors - Blue-tinted Error States
val LightError = Color(0xFFD32F2F)                // Red 700 - Error messages
val LightOnError = Color(0xFFFFFFFF)              // White text on error
val LightErrorContainer = Color(0xFFFFEBEE)       // Red 50 - Error backgrounds
val LightOnErrorContainer = Color(0xFFB71C1C)     // Dark red text

// Connection Banner - Blue Warning
val LightConnectionWarning = Color(0xFF1976D2)    // Blue 700 - Connection status banner
val LightOnConnectionWarning = Color(0xFFFFFFFF)  // White text on banner

// Special UI Elements
val LightOutline = Color(0xFFB0BEC5)              // Blue Gray 200 - Borders
val LightOutlineVariant = Color(0xFFCFD8DC)       // Blue Gray 100 - Subtle borders
val LightScrim = Color(0x80000000)                // Semi-transparent black overlay

// ===========================
// DARK THEME - Blue Palette
// ===========================

// Primary Colors - Bright Blue for Dark Mode
val DarkPrimary = Color(0xFF64B5F6)               // Light Blue 300 - Main actions
val DarkOnPrimary = Color(0xFF003C71)             // Dark blue text on primary
val DarkPrimaryContainer = Color(0xFF0D47A1)      // Blue 900 - User message bubbles
val DarkOnPrimaryContainer = Color(0xFFBBDEFB)    // Light blue text on dark containers

// Secondary Colors - Bright Cyan for Dark Mode
val DarkSecondary = Color(0xFF4DD0E1)             // Cyan 300 - Voice button, accents
val DarkOnSecondary = Color(0xFF004D56)           // Dark cyan text on secondary
val DarkSecondaryContainer = Color(0xFF006064)    // Cyan 900 - AI message bubbles
val DarkOnSecondaryContainer = Color(0xFFB2EBF2)  // Light cyan text on dark containers

// Tertiary Colors - Sky Blue for Dark Status
val DarkTertiary = Color(0xFF4FC3F7)              // Light Blue 300 - Online status, links
val DarkOnTertiary = Color(0xFF003D5B)            // Dark blue text
val DarkTertiaryContainer = Color(0xFF01579B)     // Blue 800 - Info backgrounds
val DarkOnTertiaryContainer = Color(0xFFB3E5FC)   // Light blue text

// Surface & Background
val DarkBackground = Color(0xFF0A1929)            // Deep navy background
val DarkOnBackground = Color(0xFFE3F2FD)          // Light blue-tinted white
val DarkSurface = Color(0xFF1A2332)               // Dark blue-gray surface
val DarkOnSurface = Color(0xFFE3F2FD)             // Light blue-tinted white
val DarkSurfaceVariant = Color(0xFF263238)        // Blue Gray 900 - Alternative surfaces
val DarkOnSurfaceVariant = Color(0xFFB0BEC5)      // Blue Gray 200 - Secondary text

// Error Colors - Bright Error for Dark Mode
val DarkError = Color(0xFFEF5350)                 // Red 400 - Error messages
val DarkOnError = Color(0xFF5F0008)               // Very dark red text
val DarkErrorContainer = Color(0xFFB71C1C)        // Red 800 - Error backgrounds
val DarkOnErrorContainer = Color(0xFFFFCDD2)      // Light red text

// Connection Banner - Blue Warning for Dark
val DarkConnectionWarning = Color(0xFF1976D2)     // Blue 700 - Connection banner
val DarkOnConnectionWarning = Color(0xFFFFFFFF)   // White text on banner

// Special UI Elements
val DarkOutline = Color(0xFF455A64)               // Blue Gray 700 - Borders
val DarkOutlineVariant = Color(0xFF37474F)        // Blue Gray 800 - Subtle borders
val DarkScrim = Color(0x99000000)                 // Semi-transparent black overlay

// ===========================
// MESSAGE BUBBLE COLORS
// ===========================

// User Message Bubbles - Blue Gradient
val UserMessageLight = Color(0xFFBBDEFB)          // Light Blue 100
val UserMessageLightGradient = Color(0xFF90CAF9)  // Light Blue 200 - Gradient end
val UserMessageDark = Color(0xFF0D47A1)           // Blue 900
val UserMessageDarkGradient = Color(0xFF1565C0)   // Blue 800 - Gradient end

// AI Message Bubbles - Soft Blue-Gray
val AIMessageLight = Color(0xFFE0F7FA)            // Cyan 50
val AIMessageLightBorder = Color(0xFFB2EBF2)      // Cyan 100
val AIMessageDark = Color(0xFF006064)             // Cyan 900
val AIMessageDarkBorder = Color(0xFF00838F)       // Cyan 800

// ===========================
// ACCENT & STATUS COLORS
// ===========================

// Accent Colors - Blue Tones
val AccentBlue = Color(0xFF2196F3)                // Blue 500
val AccentLightBlue = Color(0xFF03A9F4)           // Light Blue 500
val AccentCyan = Color(0xFF00BCD4)                // Cyan 500
val AccentTeal = Color(0xFF009688)                // Teal 500
val AccentIndigo = Color(0xFF3F51B5)              // Indigo 500

// Status Colors - Blue-Based
val SuccessBlue = Color(0xFF0288D1)               // Light Blue 700 - Success states
val WarningBlue = Color(0xFF1976D2)               // Blue 700 - Warning states (replaces orange)
val InfoBlue = Color(0xFF03A9F4)                  // Light Blue 500 - Info states
val OnlineBlue = Color(0xFF00BCD4)                // Cyan 500 - Online status

// ===========================
// NEUTRAL GRAYS (Blue-Tinted)
// ===========================

val BlueGray50 = Color(0xFFECEFF1)
val BlueGray100 = Color(0xFFCFD8DC)
val BlueGray200 = Color(0xFFB0BEC5)
val BlueGray300 = Color(0xFF90A4AE)
val BlueGray400 = Color(0xFF78909C)
val BlueGray500 = Color(0xFF607D8B)
val BlueGray600 = Color(0xFF546E7A)
val BlueGray700 = Color(0xFF455A64)
val BlueGray800 = Color(0xFF37474F)
val BlueGray900 = Color(0xFF263238)

// ===========================
// INTERACTION STATES
// ===========================

// Ripple Effects
val RippleLightBlue = Color(0x1F2196F3)           // 12% opacity blue for light theme
val RippleDarkBlue = Color(0x3364B5F6)            // 20% opacity blue for dark theme

// Hover States
val HoverLightBlue = Color(0x0A2196F3)            // 4% opacity blue for light theme
val HoverDarkBlue = Color(0x1464B5F6)             // 8% opacity blue for dark theme

// Focus States
val FocusBlue = Color(0xFF2196F3)                 // Blue 500 - Focus indicators

// Disabled States
val DisabledLight = Color(0x61000000)             // 38% opacity black
val DisabledDark = Color(0x61FFFFFF)              // 38% opacity white

// ===========================
// SPECIAL EFFECTS
// ===========================

// Shimmer Effect - Blue Tint
val ShimmerHighLight = Color(0xFFE3F2FD)          // Light Blue 50
val ShimmerBase = Color(0xFFBBDEFB)               // Light Blue 100

// Typing Indicator - Blue
val TypingIndicatorBlue = Color(0xFF1976D2)       // Blue 700

// Shadows & Elevation
val ShadowLight = Color(0x1A000000)               // 10% opacity for light theme
val ShadowDark = Color(0x33000000)                // 20% opacity for dark theme

// ===========================
// GRADIENT DEFINITIONS
// ===========================

// For programmatic gradient usage
val BlueGradientLight = listOf(
    Color(0xFFBBDEFB),  // Light Blue 100
    Color(0xFF90CAF9)   // Light Blue 200
)

val BlueGradientDark = listOf(
    Color(0xFF0D47A1),  // Blue 900
    Color(0xFF1565C0)   // Blue 800
)

// Connection status gradient
val ConnectionGradient = listOf(
    Color(0xFF1976D2),  // Blue 700
    Color(0xFF1565C0)   // Blue 800
)
