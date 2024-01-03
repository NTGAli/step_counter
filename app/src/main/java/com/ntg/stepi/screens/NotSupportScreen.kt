package com.ntg.stepi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.stepi.R
import com.ntg.stepi.ui.theme.Background
import com.ntg.stepi.ui.theme.SECONDARY900
import com.ntg.stepi.ui.theme.fontMedium24

@Composable
fun NotSupportScreen(){

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Background),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(modifier = Modifier.padding(top = 32.dp), text = stringResource(id = R.string.sensor_not_support), style = fontMedium24(
            SECONDARY900))

    }

}