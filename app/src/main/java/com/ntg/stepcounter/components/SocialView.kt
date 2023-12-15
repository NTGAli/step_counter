package com.ntg.stepcounter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontRegular12

@Composable
fun SocialView(
    modifier: Modifier = Modifier,
    title: String,
    url: String,
    onClick: (String) -> Unit
) {

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    onClick.invoke(url)
                }
                .padding(8.dp)
            , text = title, style = fontRegular12(MaterialTheme.colors.secondary)
        )

        Divider(
            Modifier
                .width(1.dp)
                .height(16.dp)
                .background(color = MaterialTheme.colors.onSurface, shape = RoundedCornerShape(8.dp))
        )
    }

}