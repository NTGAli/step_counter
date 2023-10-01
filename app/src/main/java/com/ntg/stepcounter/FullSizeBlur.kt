package com.ntg.stepcounter

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import dev.jakhongirmadaminov.glassmorphiccomposables.GlassmorphicColumn
import dev.jakhongirmadaminov.glassmorphiccomposables.Place
import dev.jakhongirmadaminov.glassmorphiccomposables.fastblur
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FullSizeBlur(
    modifier: Modifier = Modifier,
    alpha: Float = 1F,
    scrollState: ScrollState = rememberScrollState(),
    captureController: CaptureController = rememberCaptureController(),
    wallpaperResource: Int = R.drawable.icons8_trainers_1,
    blurRadius: Int = 100,
    color: Color = colorResource(id = R.color.background),
    scale: Float = 1f,
    strokeWidth: Float = 1f,
    content: @Composable () -> Unit
) {
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(alpha)
    ) {
        Capturable(
            controller = captureController,
            onCaptured = { bitmap, _ ->
                bitmap?.let {
                    fastblur(it.asAndroidBitmap(), scale, blurRadius)?.let { fastBlurred ->
                        capturedBitmap = fastBlurred
                    }
                }
            }

        ) {
            Image(
                painter = painterResource(id = wallpaperResource),
                contentDescription = "wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        LaunchedEffect(key1 = true, block = {
            withContext(Dispatchers.Main) {
                if (capturedBitmap == null) captureController.capture()
            }
        })


        val childMeasures = remember { mutableStateListOf(Place()) }

        capturedBitmap?.let { capturedImage ->
            GlassmorphicColumn(
                scrollState = scrollState,
                childMeasures = childMeasures,
                targetBitmap = capturedImage,
                blurRadius = blurRadius,
                drawOnTop = { path ->
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(strokeWidth),
                    )

                },
                content = {
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned {
                                childMeasures[0].apply {
                                    this.size = IntSize(it.size.width, it.size.height)
                                    this.offset = Offset(
                                        it.positionInParent().x,
                                        it.positionInParent().y
                                    )
                                }
                            }
                    ) {
                        content()
                    }
                }
            )
        }
    }
}