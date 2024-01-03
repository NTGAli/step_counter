package com.ntg.stepi.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.stepi.R
import com.ntg.stepi.ui.theme.TERTIARY500
import com.ntg.stepi.ui.theme.fontRegular12

@Composable
fun AchievementItem(
    modifier: Modifier = Modifier,
    text: String
){
    Row(modifier= Modifier.padding(horizontal = 16.dp).padding(top = 8.dp),verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = R.drawable.crown), contentDescription = null, tint = TERTIARY500)
        Text(modifier = Modifier.padding(start = 8.dp),text = text, style = fontRegular12(
            MaterialTheme.colors.secondary))
    }
}