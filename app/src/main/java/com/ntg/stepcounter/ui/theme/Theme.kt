package com.ntg.stepcounter.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.dynamicDarkColorScheme
//import androidx.compose.material3.dynamicLightColorScheme
//import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColors(
    primary = PrimaryDark,
    primaryVariant = PrimaryContainer,
    secondary = SecondaryDark,
    background = BackgroundDark,
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    error = ERROR500,
    onError = PRIMARY100,
)

private val LightColorScheme = lightColors(
    primary = PRIMARY500,
    primaryVariant = PRIMARY100,
    secondary = SECONDARY500,
    background = Background,
    surface = Color(0xFFFFFBFE),
    onPrimary = OnPrimary,
    onSecondary = Color.White,
    onBackground = OnBackground,
    onSurface = OnSurface,
    error = ERROR500,
    onError = PRIMARY100,
    secondaryVariant = SECONDARY100
)

@Composable
fun isDark() = _isDark

private var _isDark: Boolean = true

@Composable
fun StepCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    _isDark = true
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false

            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightNavigationBars = false

        }
    }

    MaterialTheme(
        typography = Typography,
        content = content,
        colors = colorScheme
    )
}