package com.ntg.stepi.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepi.models.components.ButtonType
import com.ntg.stepi.ui.theme.fontBold14

@Composable
fun Title(
    modifier: Modifier = Modifier,
    title: String,
    subText: String? = null,
    action: String,
    showBtn: Boolean = true,
    actionClick:() -> Unit
){
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, style = fontBold14(MaterialTheme.colors.secondary))
        Text(modifier = Modifier.weight(1f).padding(start = 8.dp),text = subText.orEmpty(), style = fontBold14(MaterialTheme.colors.primary))

        if (showBtn){
            CustomButton(text = action, type = ButtonType.Secondary, size = ButtonSize.XS, style = ButtonStyle.TextOnly){
                actionClick.invoke()
            }
        }
    }
}