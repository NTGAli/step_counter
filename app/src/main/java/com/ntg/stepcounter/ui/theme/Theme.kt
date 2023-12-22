package com.ntg.stepcounter.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.LocalRippleTheme
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.dynamicDarkColorScheme
//import androidx.compose.material3.dynamicLightColorScheme
//import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ntg.stepcounter.models.UserStore
import com.ntg.stepcounter.util.StepRippleTheme

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
    onError = ERROR200,
    secondaryVariant = SECONDARY500
)

private val LightColorScheme = lightColors(
    primary = PRIMARY500,
    primaryVariant = PRIMARY100,
    secondary = SECONDARY500,
    background = Background,
    surface = SECONDARY700,
    onPrimary = OnPrimary,
    onSecondary = Color.White,
    onBackground = OnBackground,
    onSurface = OnSurface,
    error = ERROR500,
    onError = ERROR900,
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

    val theme = UserStore(LocalContext.current)
    val userTheme =
        theme.getTheme.collectAsState(initial = "light")

    val colorScheme = if (userTheme.value == "light"){
        LightColorScheme
    }else{
        DarkColorScheme
    }

    val view = LocalView.current
    _isDark = userTheme.value == "dark"

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !_isDark

            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightNavigationBars = !_isDark

        }
    }

//    MaterialTheme(
//        typography = Typography,
//        content = content,
//        colors = colorScheme
//    )

    MaterialTheme(
        colors = colorScheme,
        typography = Typography,
    ) {
        CompositionLocalProvider(
            LocalRippleTheme provides StepRippleTheme,
            content = content
        )
    }
}