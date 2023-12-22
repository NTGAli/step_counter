package com.ntg.stepcounter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.R
import com.ntg.stepcounter.ui.theme.fontBold14
import com.ntg.stepcounter.ui.theme.fontRegular14
import com.ntg.stepcounter.ui.theme.fontThin12
import com.ntg.stepcounter.util.extension.daysUntilToday
import com.ntg.stepcounter.util.extension.noRippleClickable

@Composable
fun MessageItem(
    modifier: Modifier,
    id: String,
    title: String,
    text: String,
    date: String,
    isRead: Boolean,
    buttonText: String?,
    onClickButton:(String) -> Unit = {},
    parentOnClick:(String) -> Unit = {}
){

    Column(modifier = modifier
        .noRippleClickable {
            parentOnClick.invoke(id)
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, style = fontBold14(MaterialTheme.colors.secondary))
            if (!isRead){
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(CircleShape)
                        .size(6.dp)
                        .background(MaterialTheme.colors.primary)
                )
            }

        }
        
        Text(modifier = Modifier.padding(8.dp),text = text, style = fontRegular14(MaterialTheme.colors.secondary))

        Row {
            Text(modifier = Modifier.weight(1f),
                text = if (daysUntilToday(date) == 0L) {
                stringResource(id = R.string.today)
            } else if (daysUntilToday(date) == 1L) {
                stringResource(id = R.string.yestrday)
            } else {
                stringResource(
                    id = R.string.days_ago,
                    daysUntilToday(date)
                )
            }, style = fontThin12(MaterialTheme.colors.secondary))

            if (buttonText != null){
                CustomButton(text = buttonText, style = ButtonStyle.TextOnly, size = ButtonSize.SM){
                    onClickButton.invoke(id)
                }
            }
        }

        Divider(modifier = Modifier.padding(top = 8.dp),thickness = 1.dp, color = MaterialTheme.colors.onSurface)
    }


}