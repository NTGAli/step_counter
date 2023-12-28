package com.ntg.stepcounter.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.ui.theme.fontRegular12

@Composable
fun SubItem(
    modifier: Modifier = Modifier,
    image: ImageVector,
    title: String,
    text: String,
    click:() -> Unit = {}
){

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(modifier = Modifier.size(16.dp),imageVector = image, contentDescription = null, tint = MaterialTheme.colors.onSurface)
        Text(modifier = Modifier
            .padding(start = 4.dp)
            .padding(vertical = 4.dp)
            .weight(1f),text = title, style = fontRegular12(MaterialTheme.colors.secondary))
        Text(modifier = Modifier.clip(RoundedCornerShape(2.dp)).clickable { click.invoke() },text = text, style = fontRegular12(MaterialTheme.colors.secondary))
    }

}