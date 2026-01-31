package com.crossfit.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Slate,
    onSecondary = Color.White,
    background = Chalk,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Mist,
    onSurfaceVariant = Slate,
    outline = Border,
    error = Danger,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    background = Ink,
    onBackground = Chalk,
    surface = Color(0xFF1F2937),
    onSurface = Chalk,
    surfaceVariant = Color(0xFF2B3446),
    onSurfaceVariant = Color(0xFFB8C0CF),
    outline = Color(0xFF3B4255),
    error = Danger,
    onError = Color.White
)

private val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun CrossfitTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
