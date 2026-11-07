package com.kritam.mynotes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TealLightColorScheme = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF99F6E4),
    onPrimaryContainer = Color(0xFF042F2E),
    secondary = Color(0xFF14B8A6),
    onSecondary = Color.White,
    tertiary = Color(0xFFCCFBF1),
    background = Color(0xFFF4FBFA),
    surface = Color.White,
    onSurface = Color(0xFF1F2937),
    error = Color(0xFFEF4444),
    onError = Color.White
)

@Composable
fun MyNotesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TealLightColorScheme,
        content = content
    )
}
