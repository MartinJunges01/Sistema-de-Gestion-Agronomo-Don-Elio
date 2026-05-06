package com.itec.donelio.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Emerald600,
    onPrimary = Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald900,

    secondary = Stone600,
    onSecondary = Color.White,
    secondaryContainer = Stone200,
    onSecondaryContainer = Stone900,

    tertiary = Amber600,
    onTertiary = Color.White,
    tertiaryContainer = Amber50,
    onTertiaryContainer = Amber900,

    error = Red600,
    onError = Color.White,
    errorContainer = Red50,
    onErrorContainer = Red700,

    background = Stone50,
    onBackground = Stone900,

    surface = Color.White,
    onSurface = Stone900,
    surfaceVariant = Stone100,
    onSurfaceVariant = Stone600,
    surfaceContainer = Stone50,
    surfaceContainerHigh = Stone100,
    surfaceContainerHighest = Stone200,

    outline = Stone200,
    outlineVariant = Stone100,

    inverseSurface = Stone900,
    inverseOnSurface = Stone50,
    inversePrimary = Emerald400,

    scrim = Color(0xFF000000).copy(alpha = 0.32f)
)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald400,
    onPrimary = Emerald900,
    primaryContainer = Emerald700,
    onPrimaryContainer = Emerald100,

    secondary = Stone400,
    onSecondary = Stone900,
    secondaryContainer = Stone800,
    onSecondaryContainer = Stone200,

    tertiary = Amber600,
    onTertiary = Stone900,
    tertiaryContainer = Amber900,
    onTertiaryContainer = Amber100,

    error = Red600,
    onError = Color.White,
    errorContainer = Red700,
    onErrorContainer = Red50,

    background = Stone900,
    onBackground = Stone50,

    surface = Stone800,
    onSurface = Stone50,
    surfaceVariant = Stone800,
    onSurfaceVariant = Stone400,
    surfaceContainer = Stone900,
    surfaceContainerHigh = Stone800,
    surfaceContainerHighest = Stone700,

    outline = Stone700,
    outlineVariant = Stone800,

    inverseSurface = Stone50,
    inverseOnSurface = Stone900,
    inversePrimary = Emerald600,

    scrim = Color(0xFF000000).copy(alpha = 0.32f)
)

object DonElioThemeColors {
    val success: Color
        @Composable
        get() = Emerald600

    val successContainer: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Emerald700 else Emerald100

    val warning: Color
        @Composable
        get() = Amber600

    val warningContainer: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Amber900 else Amber50

    val danger: Color
        @Composable
        get() = Red600

    val dangerContainer: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Red700 else Red50
}

@Immutable
data class DonElioExtendedTheme(
    val colorScheme: ColorScheme,
    val isDark: Boolean
) {
    val success: Color get() = if (isDark) Emerald400 else Emerald600
    val successContainer: Color get() = if (isDark) Emerald700 else Emerald100
    val warning: Color get() = Amber600
    val warningContainer: Color get() = if (isDark) Amber900 else Amber50
    val danger: Color get() = Red600
    val dangerContainer: Color get() = if (isDark) Red700 else Red50
}

@Composable
fun DonElioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = 0x00000000
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            window.navigationBarColor = 0x00000000
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
