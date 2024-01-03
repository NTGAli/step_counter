package com.ntg.stepi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.stepi.R
import com.ntg.stepi.models.components.ReportWidgetType
import com.ntg.stepi.ui.theme.fontBold12
import com.ntg.stepi.ui.theme.fontRegular12
import com.ntg.stepi.util.extension.formatNumber

@Composable
fun ReportWidget(
    modifier: Modifier = Modifier,
    viewType: ReportWidgetType,
    firstText: Int,
    firstTextString: String = "",
    secondText: Int
){

    val ctx = LocalContext.current

    Box(modifier = modifier
        .height(IntrinsicSize.Min)
        .clip(RoundedCornerShape(16.dp))
        .wrapContentHeight()
        .background(MaterialTheme.colors.background)){

        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

            Row(modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colors.primaryVariant)) {
                    Icon(modifier = Modifier.padding(8.dp),painter = painterResource(id = R.drawable.icons8_trainers_1), contentDescription = null, tint = MaterialTheme.colors.primary)
                }

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = if (viewType == ReportWidgetType.Default) stringResource(id = R.string.your_record) else if (viewType == ReportWidgetType.Update) stringResource(id = R.string.version) else stringResource(id = R.string.total_steps), style = fontRegular12(MaterialTheme.colors.secondary))
                    Text(modifier = Modifier.padding(top = 2.dp), text =if (viewType == ReportWidgetType.Update) firstTextString else  ctx.getString(R.string.step_format, formatNumber(firstText.toDouble())), style = fontBold12(MaterialTheme.colors.onPrimary))

                }
            }


            Divider(modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp)
                .background(MaterialTheme.colors.onSurface)
                .height(31.dp)
                .width(1.dp))


            Row(modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colors.primaryVariant)) {
                    Icon(modifier = Modifier.padding(8.dp), painter = if (viewType == ReportWidgetType.Group) painterResource(id = R.drawable.users_profiles_minus) else if (viewType == ReportWidgetType.Profile || viewType == ReportWidgetType.Default) painterResource(id = R.drawable.calendar_02) else painterResource(id = R.drawable.arrow_down_square_contained), contentDescription = null, tint = MaterialTheme.colors.primary)
                }

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = if (viewType == ReportWidgetType.Default) stringResource(id = R.string.day) else if (viewType == ReportWidgetType.Update) stringResource(
                        id = R.string.size
                    ) else if (viewType == ReportWidgetType.Group) stringResource(id = R.string.number_of_members) else stringResource(id = R.string.days), style = fontRegular12(MaterialTheme.colors.secondary))
                    Text(modifier = Modifier.padding(top = 2.dp),text = if (viewType == ReportWidgetType.Default) {if (secondText == 0) stringResource(
                        id = R.string.today
                    ) else if (secondText != -1)  ctx.getString(R.string.days_ago, secondText.toString()) else ctx.getString(R.string.no_record)} else if (viewType == ReportWidgetType.Profile){ if (secondText != -1) ctx.getString(R.string.days_format, secondText.toString()) else "-"} else if (viewType == ReportWidgetType.Update) stringResource(
                        id = R.string.mb_format, secondText.toString()
                    ) else secondText.toString() , style = fontBold12(MaterialTheme.colors.onPrimary))

                }
            }

        }
    }



}


@Preview
@Composable
private fun ReportWidgetPreview(){
    ReportWidget(viewType = ReportWidgetType.Update, firstText = 1200, secondText = 120)
}