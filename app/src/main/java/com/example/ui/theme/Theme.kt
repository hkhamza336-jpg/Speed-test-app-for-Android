package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SpeedDarkColorScheme =
  darkColorScheme(
    primary = SpeedCyan,
    onPrimary = Color.Black,
    primaryContainer = SpeedDarkCard,
    onPrimaryContainer = SpeedCyan,
    secondary = SpeedViolet,
    onSecondary = Color.White,
    secondaryContainer = SpeedSurfaceElevated,
    onSecondaryContainer = SpeedTextPrimary,
    tertiary = SpeedEmerald,
    onTertiary = Color.Black,
    background = SpeedDarkBg,
    onBackground = SpeedTextPrimary,
    surface = SpeedDarkBg,
    onSurface = SpeedTextPrimary,
    surfaceVariant = SpeedDarkCard,
    onSurfaceVariant = SpeedTextSecondary,
    outline = SpeedDarkCardStroke,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = SpeedDarkColorScheme,
    typography = Typography,
    content = content
  )
}

