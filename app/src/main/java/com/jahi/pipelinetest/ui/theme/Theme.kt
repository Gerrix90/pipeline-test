package com.jahi.pipelinetest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = TertiaryColor,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Slate100,
    onSecondary = Slate900,
    onTertiary = Slate900,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    outline = OutlineDark
)

@Composable
fun PipelineTestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
