package com.example.speedy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.example.speedy.ThemePreferenceManager



data class ThemeColors(
    val light: ThemeVariant,
    val dark: ThemeVariant
)

data class ThemeVariant(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val onPrimary: Color = Color.White, // Default font/icon color for primary
    val onSecondary: Color = Color.White, // Default font/icon color for secondary
    val onBackground: Color = Color.White // Default font/icon color for background
)

val ThemeVariants = mapOf(
    "Ocean" to ThemeColors(
        light = ThemeVariant(Color(0xFF03A9F4), Color(0xFF0288D1), Color(0xFFE1F5FE)),
        dark = ThemeVariant(
            primary = Color(0xFF0288D1),
            secondary = Color(0xFF01579B),
            background = Color(0xFF263238),
            onPrimary = Color.White,
            onSecondary = Color(0xFFB3E5FC),
            onBackground = Color(0xFFECEFF1)
        )
    ),
    "Default" to ThemeColors(
        light = ThemeVariant(Color(0xFF6200EE), Color(0xFF3700B3), Color(0xFFFFFFFF)),
        dark = ThemeVariant(
            primary = Color(0xFFBB86FC),
            secondary = Color(0xFF3700B3),
            background = Color(0xFF121212),
            onPrimary = Color.Black,
            onSecondary = Color(0xFFEDE7F6),
            onBackground = Color(0xFFB0BEC5)
        )
    ),
    "Sunset" to ThemeColors(
        light = ThemeVariant(Color(0xFFFF9800), Color(0xFFF57C00), Color(0xFFFFF3E0)),
        dark = ThemeVariant(
            primary = Color(0xFFF57C00),
            secondary = Color(0xFFE65100),
            background = Color(0xFF3E2723),
            onPrimary = Color.Black,
            onSecondary = Color(0xFFFFCCBC),
            onBackground = Color(0xFFD7CCC8)
        )
    ),
    "Forest" to ThemeColors(
        light = ThemeVariant(Color(0xFF4CAF50), Color(0xFF388E3C), Color(0xFFE8F5E9)),
        dark = ThemeVariant(
            primary = Color(0xFF388E3C),
            secondary = Color(0xFF1B5E20),
            background = Color(0xFF2E3B31),
            onPrimary = Color.White,
            onSecondary = Color(0xFFC8E6C9),
            onBackground = Color(0xFFE0F2F1)
        )
    ),
    "Rose" to ThemeColors(
        light = ThemeVariant(Color(0xFFE91E63), Color(0xFFC2185B), Color(0xFFFCE4EC)),
        dark = ThemeVariant(
            primary = Color(0xFFC2185B),
            secondary = Color(0xFF880E4F),
            background = Color(0xFF3E2723),
            onPrimary = Color.White,
            onSecondary = Color(0xFFF8BBD0),
            onBackground = Color(0xFFD7CCC8)
        )
    )
)

@Composable
fun SpeedyMaterialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val selectedTheme = ThemePreferenceManager.getThemeName(context).collectAsState(initial = "Default").value
    val colorScheme = remember(selectedTheme, darkTheme) {
        val themeColors = ThemeVariants[selectedTheme] ?: ThemeVariants["Default"]!!
        if (darkTheme) {
            darkColorScheme(
                primary = themeColors.dark.primary,
                onPrimary = themeColors.dark.onPrimary,
                secondary = themeColors.dark.secondary,
                onSecondary = themeColors.dark.onSecondary,
                background = themeColors.dark.background,
                onBackground = themeColors.dark.onBackground
            )
        } else {
            lightColorScheme(
                primary = themeColors.light.primary,
                onPrimary = themeColors.light.onPrimary,
                secondary = themeColors.light.secondary,
                onSecondary = themeColors.light.onSecondary,
                background = themeColors.light.background,
                onBackground = themeColors.light.onBackground
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
