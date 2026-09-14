package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SmmPrimary,
    onPrimary = TextPrimary,
    primaryContainer = SmmDarkCard,
    onPrimaryContainer = SmmPrimaryLight,
    secondary = SmmSecondary,
    onSecondary = SmmDarkBackground,
    secondaryContainer = SmmDarkCard,
    tertiary = SmmGold,
    background = SmmDarkBackground,
    onBackground = TextPrimary,
    surface = SmmDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = SmmDarkCard,
    onSurfaceVariant = TextSecondary,
    outline = SmmDarkCardBorder,
    error = SmmRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent Goriber SMM branded aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
