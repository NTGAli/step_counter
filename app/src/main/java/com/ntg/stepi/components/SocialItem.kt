package com.ntg.stepi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.stepi.models.components.PopupItem
import com.ntg.stepi.R
import com.ntg.stepi.ui.theme.fontRegular12

@Composable
fun SocialItem(
    modifier: Modifier = Modifier,
    id: Int,
    title: String,
    itemClick: (Int) -> Unit,
    edit: (Int) -> Unit,
    delete: (Int) -> Unit,
) {


    val popupItems = listOf(
        PopupItem(
            id = 0,
            icon = painterResource(id = R.drawable.edit_16_1_5),
            title = stringResource(id = R.string.edit),
        ),
        PopupItem(
            id = 1,
            icon = painterResource(id = R.drawable.trash_16_1_5),
            title = stringResource(id = R.string.delete)
        )
    )


    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                itemClick.invoke(id)
            }) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = title,
                style = fontRegular12(MaterialTheme.colors.secondary)
            )
        }
//        Box(modifier = Modifier
//            .clip(RoundedCornerShape(4.dp))
//            .clickable {
//                edit.invoke(title)
//            }) {
//            Icon(modifier = Modifier.padding(4.dp),painter = painterResource(id = R.drawable.pencil_01), contentDescription = null)
//        }

        Popup(
            popupItems = popupItems
        ) {
            when (it) {
                0 -> {
                    edit.invoke(id)
                }

                else -> {
                    delete.invoke(id)
                }
            }
        }
    }


}