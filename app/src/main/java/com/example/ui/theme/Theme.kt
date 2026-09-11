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
    primary = CrimsonRed,
    onPrimary = Color.White,
    secondary = MintIce,
    onSecondary = DeepNavy,
    tertiary = SteelBlue,
    background = Color(0xFF121417),
    surface = Color(0xFF1E2126),
    onBackground = Color.White,
    onSurface = Color.White,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CrimsonRed,
    onPrimary = Color.White,
    secondary = DeepNavy,
    onSecondary = Color.White,
    tertiary = SteelBlue,
    background = OffWhiteBg,
    surface = SurfaceCard,
    onBackground = DeepNavy,
    onSurface = DeepNavy,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextMuted,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
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
