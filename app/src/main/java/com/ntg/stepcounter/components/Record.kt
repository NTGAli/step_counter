package com.ntg.stepcounter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.R
import com.ntg.stepcounter.ui.theme.Background
import com.ntg.stepcounter.ui.theme.ERROR900
import com.ntg.stepcounter.ui.theme.SECONDARY100
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.TERTIARY500
import com.ntg.stepcounter.ui.theme.fontMedium12
import com.ntg.stepcounter.ui.theme.fontMedium14

@Composable
fun Record(
    modifier: Modifier = Modifier,
    uid: String?,
    record: Int,
    title: String,
    steps: Int?,
    onClick:(String?) -> Unit
){

    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Background)
        .border(width = 1.dp, shape = RoundedCornerShape(16.dp), color = SECONDARY100)
        .clickable {
            onClick.invoke(uid)
        }){

        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {

            Box(modifier = Modifier.padding(end = 8.dp)) {
                when(record){
                    0 -> {
                        Icon(painter = painterResource(id = R.drawable.crown), contentDescription = null, tint = TERTIARY500 )
                    }

                    1 -> {
                        Icon(painter = painterResource(id = R.drawable.second), contentDescription = null, tint = SECONDARY500 )
                    }

                    2 -> {
                        Icon(painter = painterResource(id = R.drawable.third), contentDescription = null, tint = ERROR900 )
                    }

                    else -> {
                        Text(text = record.toString(), style = fontMedium14(SECONDARY500))
                    }
                }
            }

            Text(modifier = Modifier.weight(1f), text = title, style = fontMedium14(SECONDARY500))

            Text(text = stringResource(
                id = R.string.step_format,
                steps.toString()
            ), style = fontMedium12(SECONDARY500))

        }
    }

}