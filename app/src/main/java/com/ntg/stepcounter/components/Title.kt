package com.ntg.stepcounter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.models.components.ButtonType
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontBold14

@Composable
fun Title(
    modifier: Modifier = Modifier,
    title: String,
    action: String,
    showBtn: Boolean = true,
    actionClick:() -> Unit
){
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = Modifier.weight(1f),text = title, style = fontBold14(MaterialTheme.colors.secondary))

        if (showBtn){
            CustomButton(text = action, type = ButtonType.Secondary, size = ButtonSize.XS, style = ButtonStyle.TextOnly){
                actionClick.invoke()
            }
        }
    }
}