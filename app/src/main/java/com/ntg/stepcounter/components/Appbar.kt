package com.ntg.stepcounter.components

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.OnGloballyPositionedModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Appbar(
    modifier: Modifier = Modifier,
    onGloballyPos:(Float) -> Unit = {},
){

    TopAppBar(modifier = modifier
        .onGloballyPositioned { layoutCoordinates ->
            val a = layoutCoordinates.size.height
            onGloballyPos.invoke(a.toFloat())
        },
//        backgroundColor = Color(topBarColor.red, topBarColor.blue, topBarColor.green),
        title = {
            Text(text = "Wassertemperaturen", fontSize = 20.sp)

        },
        elevation = 0.dp
    )


}