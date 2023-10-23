package com.ntg.stepcounter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.models.components.ButtonType
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.SECONDARY100
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontMedium12

@Composable
fun PermissionItem(
    modifier: Modifier = Modifier,
    id: Int,
    icon: Painter,
    text: String,
    btnText: String,
    isGranted: Boolean,
    onClick:(Int) -> Unit
){

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = icon, contentDescription = null, tint = PRIMARY500)

            Text(modifier = Modifier
                .padding(start = 16.dp)
                .padding(vertical = 24.dp)
                .weight(1f), text = text, style = fontMedium12(SECONDARY500))

            if (isGranted){
                Icon(imageVector = Icons.Rounded.Check, contentDescription = null, tint = PRIMARY500)
            }else{
                CustomButton(text = btnText, style = ButtonStyle.Outline, size = ButtonSize.XS, type = ButtonType.Primary){
                    onClick.invoke(id)
                }
            }

        }

        Divider(modifier = Modifier
            .padding(start = 40.dp)
            .height(1.dp)
            .background(SECONDARY100))
    }

}