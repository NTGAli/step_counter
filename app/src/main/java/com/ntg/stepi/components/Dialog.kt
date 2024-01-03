package com.ntg.stepi.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import com.ntg.stepi.R
import com.ntg.stepi.ui.theme.PRIMARY500
import com.ntg.stepi.ui.theme.SECONDARY500
import com.ntg.stepi.ui.theme.SECONDARY900
import com.ntg.stepi.ui.theme.fontMedium12
import com.ntg.stepi.ui.theme.fontMedium16
import com.ntg.stepi.ui.theme.fontRegular12

@Composable
fun Dialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: Painter,
) {
    AlertDialog(
        icon = {
            Icon(painter = icon, contentDescription = "Icon")
        },
        title = {
            Text(text = dialogTitle, style = fontMedium16(SECONDARY900))
        },
        text = {
            Text(text = dialogText, style = fontMedium12(SECONDARY500))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(id = R.string.access),style = fontRegular12(PRIMARY500))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.close), style = fontRegular12(PRIMARY500))
            }
        }
    )
}