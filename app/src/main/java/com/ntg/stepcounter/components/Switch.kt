package com.ntg.stepcounter.components

import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ntg.stepcounter.ui.theme.PRIMARY200
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.SECONDARY200
import com.ntg.stepcounter.ui.theme.SECONDARY400

@Composable
fun Switch(
    modifier: Modifier = Modifier,
    checked: MutableState<Boolean> = remember { mutableStateOf(true) }
){
    androidx.compose.material.Switch(
        modifier = modifier,
        checked = checked.value,
        onCheckedChange = {
            checked.value = it
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = PRIMARY500,
            checkedTrackColor = PRIMARY200,
            uncheckedThumbColor = SECONDARY400,
            uncheckedTrackColor= SECONDARY200,
        )
    )
}