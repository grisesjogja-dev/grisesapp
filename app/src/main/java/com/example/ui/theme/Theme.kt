package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GrisesGreenPrimaryDark,
    onPrimary = GrisesGreenDark,
    primaryContainer = GrisesGreenContainerDark,
    onPrimaryContainer = GrisesGreenPrimaryDark,
    secondary = GrisesGoldPrimary,
    onSecondary = Color.Black,
    secondaryContainer = GrisesOnGoldContainer,
    onSecondaryContainer = GrisesGoldContainer,
    tertiary = GrisesTealLight,
    onTertiary = Color.Black,
    tertiaryContainer = GrisesOnTealContainer,
    onTertiaryContainer = GrisesTealContainer,
    background = GrisesBackgroundDark,
    surface = GrisesSurfaceDark,
    surfaceVariant = GrisesSurfaceVariantDark,
    onBackground = GrisesTextPrimaryDark,
    onSurface = GrisesTextPrimaryDark,
    onSurfaceVariant = GrisesTextSecondaryDark,
    outline = GrisesGreenPrimaryDark.copy(alpha = 0.4f),
    outlineVariant = GrisesSurfaceVariantDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GrisesGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GrisesGreenContainer,
    onPrimaryContainer = GrisesOnGreenContainer,
    secondary = GrisesGoldPrimary,
    onSecondary = Color.White,
    secondaryContainer = GrisesGoldContainer,
    onSecondaryContainer = GrisesOnGoldContainer,
    tertiary = GrisesTeal,
    onTertiary = Color.White,
    tertiaryContainer = GrisesTealContainer,
    onTertiaryContainer = GrisesOnTealContainer,
    background = GrisesBackgroundLight,
    surface = GrisesSurfaceLight,
    surfaceVariant = GrisesSurfaceVariantLight,
    onBackground = GrisesTextPrimaryLight,
    onSurface = GrisesTextPrimaryLight,
    onSurfaceVariant = GrisesTextSecondaryLight,
    outline = Color(0xFFA7F3D0),
    outlineVariant = Color(0xFFD1FAE5)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Keep intentional bright branding default
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
