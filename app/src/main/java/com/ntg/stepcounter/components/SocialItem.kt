package com.ntg.stepcounter.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.R
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontRegular12

@Composable
fun SocialItem(
    modifier: Modifier = Modifier,
    title: String,
    itemClick: (String) -> Unit,
    edit: (String) -> Unit,
    delete: (String) -> Unit,
) {

    Row(modifier = modifier) {
        Text(modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                itemClick.invoke(title)
            }, text = title, style = fontRegular12(SECONDARY500))
        Box(modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                edit.invoke(title)
            }) {
            Icon(painter = painterResource(id = R.drawable.pencil_01), contentDescription = null)
        }
    }


}