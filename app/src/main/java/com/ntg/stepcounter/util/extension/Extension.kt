package com.ntg.stepcounter.util.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale


fun Float?.orZero() = this ?: 0f
fun Long?.orDefault() = this ?: 0L
fun String?.orDefault() = this ?: ""
fun Int?.orZero() = this ?: 0
fun Boolean?.orFalse() = this ?: false
fun Boolean?.orTrue() = this ?: true


fun timber(msg: String) {
    Timber.d(msg)
}

fun timber(title: String, msg: String) {
    Timber.d("$title ----------> $msg")
}

fun formatNumber(number: Double): String {
    return when {
        number < 1000 -> number.toString()
        number < 1_000_000 -> {
            if (number % 1000.0 == 0.0) {
                "${number / 1000} هزار"
            } else {
                String.format("%.1f هزار", number / 1000.0)
            }
        }
        else -> {
            if (number % 1_000_000 == 0.0) {
                "${number / 1_000_000} میلیون"
            } else {
                String.format("%.1f میلیون", number / 1_000_000.0)
            }
        }
    }
}


fun divideNumber(number: Int): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    return numberFormat.format(number)
}

fun stepsToKilometers(steps: Int): String {
    // Conversion factor: On average, 1 step is approximately 0.000762 kilometers (or 76.2 cm).
    val kilometers = steps * 0.000762
    return formatNumber("%.2f".format(kilometers).toDouble())
}

fun stepsToCalories(steps: Int): String {
    // Conversion factor: On average, walking burns about 0.035 calories per step.
    val calories = steps * 0.035
    return formatNumber("%.2f".format(calories).toDouble())
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}