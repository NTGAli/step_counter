package com.ntg.stepcounter.util.extension

import java.text.NumberFormat
import java.util.Locale

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