package com.ntg.stepi.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.asLiveData
import com.ntg.stepi.models.UserStore
import com.ntg.stepi.util.StepRippleTheme
import com.ntg.stepi.util.extension.timber
import java.util.Locale


private val DarkColorScheme = darkColors(
    primary = PRIMARY300,
    primaryVariant = PrimaryContainer,
    secondary = SecondaryDark,
    background = BackgroundDark,
    surface = Color(0xFFFFFBFE),
    onPrimary = PRIMARY900,
    onSecondary = SECONDARY200,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    error = ERROR500,
    onError = ERROR200,
    secondaryVariant = SECONDARY500,
)

private val LightColorScheme = lightColors(
    primary = PRIMARY500,
    primaryVariant = PRIMARY100,
    secondary = SECONDARY500,
    background = Background,
    surface = SECONDARY700,
    onPrimary = Color.White,
    onSecondary = SECONDARY900,
    onBackground = OnBackground,
    onSurface = OnSurface,
    error = ERROR500,
    onError = ERROR900,
    secondaryVariant = SECONDARY100
)

fun isDark() = _isDark

private var _isDark: Boolean = true

@Composable
fun StepCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val userData = UserStore(LocalContext.current)
    val userTheme =
        userData.getTheme.collectAsState(initial = "default")



    val colorScheme = if (userTheme.value == "default"){
        if (darkTheme) DarkColorScheme else LightColorScheme
    }else if (userTheme.value == "light"){
        LightColorScheme
    }else DarkColorScheme

    _isDark = colorScheme == DarkColorScheme
    val view = LocalView.current

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

    val configuration = LocalConfiguration.current
    val resources = LocalContext.current.resources


    userData.getLanguage.asLiveData().observe(LocalLifecycleOwner.current){lang ->
        timber("AppLanguage %s", lang)

        val locale = Locale(lang)
        Locale.setDefault(locale)
        configuration.setLocale(locale)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

    }
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