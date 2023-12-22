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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.ui.theme.fontMedium14

@Composable
fun RoleItem(modifier: Modifier,id: Int = 0, title: String, onClick:(Int) -> Unit){
    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(
            width = 1.dp,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.onSurface
        )
        .background(MaterialTheme.colors.primary)
        .clickable {
            onClick.invoke(id)
        }){

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colors.onSurface
            )
            .background(MaterialTheme.colors.background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp).weight(1f), text = title, style = fontMedium14(MaterialTheme.colors.secondary))

            Icon(modifier = Modifier.padding(end = 8.dp),
                imageVector = Icons.Rounded.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colors.secondary)
        }
    }

}

@Preview
@Composable
private fun RoleItemPreview(){
    RoleItem(modifier = Modifier, id = 1, title = "تست", onClick = {})
}