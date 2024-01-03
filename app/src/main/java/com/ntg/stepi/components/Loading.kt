package com.ntg.stepi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Loading(
    isFull: Boolean = true
){
    var modifier by remember {
        mutableStateOf(Modifier.fillMaxSize())
    }

    if (!isFull) modifier = Modifier.fillMaxWidth().height(250.dp)

    Box(modifier = modifier.background(color = MaterialTheme.colors.background), contentAlignment = Alignment.Center){

        CircularProgressIndicator(modifier = Modifier
            .progressSemantics()
            .size(24.dp)
            , color = MaterialTheme.colors.secondary, strokeWidth = 3.dp)

    }
}