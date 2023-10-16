package com.ntg.stepcounter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.ui.theme.Background
import com.ntg.stepcounter.ui.theme.SECONDARY500

@Composable
fun Loading(
    isFull: Boolean = true
){
    var modifier by remember {
        mutableStateOf(Modifier.fillMaxSize())
    }

    if (!isFull) modifier = Modifier.fillMaxWidth().height(250.dp)

    Box(modifier = modifier.background(color = Background), contentAlignment = Alignment.Center){

        CircularProgressIndicator(modifier = Modifier
            .progressSemantics()
            .size(24.dp)
            , color = SECONDARY500, strokeWidth = 3.dp)

    }
}