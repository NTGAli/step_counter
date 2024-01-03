package com.ntg.stepi.ui.theme

//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.dynamicDarkColorScheme
//import androidx.compose.material3.dynamicLightColorScheme
//import androidx.compose.material3.lightColorScheme

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
    primary = PrimaryDark,
    primaryVariant = PrimaryContainer,
    secondary = SecondaryDark,
    background = BackgroundDark,
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
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
    onPrimary = OnPrimary,
    onSecondary = SECONDARY900,
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

    val userData = UserStore(LocalContext.current)
    val userTheme =
        userData.getTheme.collectAsState(initial = "light")

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

    val ctx = LocalContext.current
    val configuration = LocalConfiguration.current
    var resources = LocalContext.current.resources


    userData.getLanguage.asLiveData().observe(LocalLifecycleOwner.current){lang ->
//        try {
//        } catch (e: Exception) {
//            lang = "fa"
//        }

//        ctx.findActivity()?.runOnUiThread {
//            val appLocale = LocaleListCompat.forLanguageTags(lang) //here ta is hardcoded for testing purpose,you can add users selected language code.
//            AppCompatDelegate.setApplicationLocales(appLocale)
//        }
        timber("AppLanguage %s", lang)
//        val appLocale = LocaleListCompat.forLanguageTags(lang)
//        AppCompatDelegate.setApplicationLocales(appLocale)

        val locale = Locale(lang)
        Locale.setDefault(locale)
        configuration.setLocale(locale)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

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