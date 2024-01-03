package com.ntg.stepi.util.extension

import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ntg.stepi.R
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.models.Failure
import com.ntg.stepi.models.RGBColor
import com.ntg.stepi.models.Result
import com.ntg.stepi.models.Success
import com.ntg.stepi.services.StepCounterService
import com.ntg.stepi.ui.theme.isDark
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale


fun Float?.orZero() = this ?: 0f
fun Long?.orDefault() = this ?: 0L
fun String?.orDefault() = this ?: ""
fun Int?.orZero() = this ?: 0
fun Double?.orZero() = this ?: 0.0
fun Boolean?.orFalse() = this ?: false
fun Boolean?.orTrue() = this ?: true
fun Int?.orNull() = this ?: null


fun timber(msg: String) {
    Timber.d(msg)
}

fun timber(title: String, msg: String) {
    Timber.d("$title ----------> $msg")
}

fun Context.toast(mag: String) {
    Toast.makeText(this, mag, Toast.LENGTH_SHORT).show()
}

fun formatNumber(number: Double): String {
    return when {
        number < 1000 -> number.toInt().toString()
        number < 1_000_000 -> {
            if (number % 1000.0 == 0.0) {
                "${(number / 1000).toInt()} هزار"
            } else {
                String.format("%.1f هزار", number / 1000)
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

fun Context.stepsToKilometers(steps: Int): String {
    // Conversion factor: On average, 1 step is approximately 0.000762 kilometers (or 76.2 cm).
    val kilometers = steps * 0.000762
    return try {
        if (kilometers >= 1.0){
            this.getString(R.string.km_format, formatNumber(kilometers))
        }else this.getString(R.string.meter_format, formatNumber(kilometers * 1000))
    } catch (e: Exception) {
        "0"
    }
}

fun stepsToTime(steps: Int): Long {
    // Conversion factor: On average, 1 step is approximately 0.000762 kilometers (or 76.2 cm).
    return (steps * 0.8).toLong()
}

fun stepsToCalories(steps: Int): String {
    // Conversion factor: On average, walking burns about 0.035 calories per step.
    val calories = steps * 0.035
    return try {
        formatNumber(calories)
    } catch (e: Exception) {
        "0"
    }
}

fun Long.toReadableTime(ctx: Context): String {
    return if (this <= 60) ctx.getString(R.string.sec_format, this.toString())
    else if (this <= 3600) ctx.getString(R.string.min_format, (this / 60).toString())
    else ctx.getString(R.string.hour_format, (this / 3600).toString())
}

fun daysUntilToday(targetDateStr: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = Date()
    return try {
        val targetDate = dateFormat.parse(targetDateStr)
        val difference = currentDate.time - targetDate.time
        difference / (1000 * 60 * 60 * 24)
    } catch (e: Exception) {
        e.printStackTrace()
        -1 // Return -1 to indicate an error
    }
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

fun Context.sendMail(to: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "vnd.android.cursor.item/email" // or "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        this.toast(getString(R.string.sth_wrong))
    } catch (t: Throwable) {
        this.toast(getString(R.string.sth_wrong))
    }
}

fun String.isValidIranMobileNumber(): Boolean {
    val regex = Regex("^09\\d{9}$")
    return !regex.matches(this)
}

fun notEmptyOrNull(
    value: String?,
    errorMessage: String = "error!"
): Result<String> =
    if (value != "" && value != null) {
        Success(value)
    } else {
        Failure(errorMessage)
    }

fun notEmptyOrNull(
    value: Int?,
    errorMessage: String = "error!"
): Result<Int> =
    if (value != -1 && value != null) {
        Success(value)
    } else {
        Failure(errorMessage)
    }

fun notNull(
    value: Any?,
    errorMessage: String = "error!"
): Result<Any> =
    if (value != null) {
        Success(value)
    } else {
        Failure(errorMessage)
    }

fun dateOfToday(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val z = ZoneId.of("Asia/Tehran")
    val zdt = ZonedDateTime.now(z)
    zdt.toLocalDate().toString()
} else {
    val currentDate = Date()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    dateFormat.format(currentDate)
}

fun calculateRadius(first: Float, end: Float, third: Float): Float {
    if (third > end) return 32f
    if (third < first) return 0f
    val range = end - first
    val normalizedThird = third - first
    return ((normalizedThird.toDouble() / range.toDouble()) * 32).toFloat()
}

@Composable
fun getColorComponentsForNumber(number: Int): RGBColor {
    require(number in 0..32) { "Number must be between 0 and 32" }


    val startColor = if (!isDark()) {
        RGBColor(255, 255, 255)
    } else {
        RGBColor(25, 28, 30)
    }

    val endColor = if (!isDark()) {
        RGBColor(248, 248, 248)
    } else {
        RGBColor(37, 41, 44)
    }

    val interpolatedRed = startColor.red + (endColor.red - startColor.red) * number / 32
    val interpolatedGreen = startColor.green + (endColor.green - startColor.green) * number / 32
    val interpolatedBlue = startColor.blue + (endColor.blue - startColor.blue) * number / 32

    return RGBColor(interpolatedRed, interpolatedGreen, interpolatedBlue)
}

fun Context.foregroundServiceRunning(): Boolean {
    val activityManager = this.getSystemService(ComponentActivity.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getRunningServices(Int.MAX_VALUE).forEach {
        timber("SERVICES_RUNNING :::: ${it.service.className} -- ${StepCounterService::class.java.name}")
        if (StepCounterService::class.java.name == it.service.className) {
            return true
        }
    }
    return false
}

fun List<String>.diffNum(list: List<String>): Int {
    val maxSize = maxOf(this.size, list.size)
    var differences = 0

    for (i in 0 until maxSize) {
        val element1 = this.getOrNull(i) ?: "" // Use empty string if index is out of bounds
        val element2 = list.getOrNull(i) ?: "" // Use empty string if index is out of bounds

        if (element1 != element2) {
            differences++
        }
    }

    return differences
}

fun List<String>.nor(list: List<String>) = (list - this).size

//fun randStr(length: Int = 5) : String {
//    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
//    return (1..length)
//        .map { allowedChars.random() }
//        .joinToString("")
//}

fun Context.checkInternet(): Boolean {
    return (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null
}

fun Context.openInBrowser(url: String){
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    this.startActivity(browserIntent)
}

fun Context.findActivity() : Activity? = when(this){
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun getBitmapFromView(view: View): Bitmap? {
    //Define a bitmap with the same size as the view
    val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    //Bind a canvas to it
    val canvas = Canvas(returnedBitmap)
    //Get the view's background
    val bgDrawable = view.background
    if (bgDrawable != null) //has background drawable, then draw it on the canvas
        bgDrawable.draw(canvas) else  //does not have background drawable, then draw white background on the canvas
        canvas.drawColor(Color.WHITE)
    // draw the view on the canvas
    view.draw(canvas)
    //return the bitmap
    return returnedBitmap
}

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiToBeCalled: suspend () -> Response<T>
): LiveData<NetworkResult<T>> {


    return liveData(dispatcher) {

        var response: Response<T>? = null
        try {
            emit(NetworkResult.Loading())
            timber("TREE_RES_DATE ::: SF1 $response")

            response = apiToBeCalled.invoke()

            timber("TREE_RES_DATE ::: SF $response")

            if (response.isSuccessful) {
                emit(NetworkResult.Success(data = response.body()))
            } else {
                emit(
                    NetworkResult.Error(message = response.errorBody().toString())
                )

            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error(message = "HttpException ::: ${e.message}"))
        } catch (e: IOException) {
            emit(NetworkResult.Error(message = "IOException ::: ${e.message}"))
        } catch (e: Exception) {
            emit(NetworkResult.Error(message = "Exception ::: ${e.message}"))
        }
    }
}
