package com.ntg.stepcounter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.ui.theme.Background
import com.ntg.stepcounter.ui.theme.SECONDARY500

@Composable
fun Loading(){
    Box(modifier = Modifier.fillMaxSize().background(color = Background), contentAlignment = Alignment.Center){

        CircularProgressIndicator(modifier = Modifier
            .progressSemantics()
            .size(24.dp)
            , color = SECONDARY500, strokeWidth = 3.dp)

    }
}