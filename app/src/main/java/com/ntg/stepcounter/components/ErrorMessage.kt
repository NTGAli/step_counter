package com.ntg.stepcounter.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.R
import com.ntg.stepcounter.models.ErrorStatus
import com.ntg.stepcounter.ui.theme.ERROR500
import com.ntg.stepcounter.ui.theme.TERTIARY500

@Composable
fun ErrorMessage(
    modifier: Modifier = Modifier,
    status: ErrorStatus,
    onClick:() -> Unit
){

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
        Icon(painter = if (status == ErrorStatus.Failed) painterResource(id = R.drawable.alert_triangle) else painterResource(
            id = R.drawable.wifi_off
        ),
            tint = if (status == ErrorStatus.Failed) TERTIARY500 else ERROR500, contentDescription = null)
        
        Text(modifier = Modifier.padding(top = 8.dp),text = if (status == ErrorStatus.Failed) stringResource(id = R.string.fiald_to_fetch_data) else stringResource(
            id = R.string.no_internet
        ))
        
        CustomButton(modifier = Modifier.padding(top = 8.dp),text = stringResource(id = R.string.try_again), style = ButtonStyle.TextOnly, size = ButtonSize.MD){
            onClick.invoke()
        }
    }

}