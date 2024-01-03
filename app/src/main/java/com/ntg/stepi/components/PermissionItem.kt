package com.ntg.stepi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepi.models.components.ButtonType
import com.ntg.stepi.ui.theme.fontMedium12

@Composable
fun PermissionItem(
    modifier: Modifier = Modifier,
    id: Int,
    icon: Painter,
    text: String,
    btnText: String,
    isGranted: Boolean,
    permissionDestination: String,
    onClick:(Int) -> Unit
){

    var openDialog by remember {
        mutableStateOf(false)
    }

    if (openDialog){
        Dialog(
            onDismissRequest = {
                openDialog = false
            },
            onConfirmation = {
                onClick.invoke(id)
                openDialog = false
            },
            dialogTitle = text,
            dialogText = permissionDestination,
            icon = icon
        )
    }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = icon, contentDescription = null, tint = MaterialTheme.colors.primary)

            Text(modifier = Modifier
                .padding(start = 16.dp)
                .padding(vertical = 24.dp)
                .weight(1f), text = text, style = fontMedium12(MaterialTheme.colors.secondary))


            if (isGranted){
                Icon(imageVector = Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colors.primary)
            }else{
                CustomButton(text = btnText, style = ButtonStyle.Outline, size = ButtonSize.XS, type = ButtonType.Primary){
                    openDialog = true
                }
            }

        }

        Divider(modifier = Modifier
            .padding(start = 40.dp)
            .height(1.dp)
            .background(MaterialTheme.colors.onSurface))
    }

}