package com.ntg.stepcounter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.R
import com.ntg.stepcounter.models.components.ReportWidgetType
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.SECONDARY900
import com.ntg.stepcounter.ui.theme.fontBold12
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.util.extension.formatNumber

@Composable
fun ReportWidget(
    modifier: Modifier = Modifier,
    viewType: ReportWidgetType,
    firstText: Int,
    secondText: Int
){

    val ctx = LocalContext.current

    Box(modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .wrapContentHeight()
        .background(Color.White)){

        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

            Row(modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(PRIMARY100)) {
                    Icon(modifier = Modifier.padding(8.dp),painter = if (viewType == ReportWidgetType.Group) painterResource(id = R.drawable.users_profiles_minus) else painterResource(id = R.drawable.icons8_trainers_1), contentDescription = null, tint = PRIMARY500)
                }

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = if (viewType == ReportWidgetType.Default) stringResource(id = R.string.your_record) else stringResource(id = R.string.steps), style = fontRegular12(SECONDARY500))
                    Text(modifier = Modifier.padding(top = 2.dp), text = ctx.getString(R.string.step_format, formatNumber(firstText.toDouble())), style = fontBold12(SECONDARY900))

                }
            }


            Divider(modifier = Modifier
                .padding(horizontal = 12.dp)
                .height(31.dp)
                .width(1.dp))


            Row(modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(PRIMARY100)) {
                    Icon(modifier = Modifier.padding(8.dp), painter = if (viewType == ReportWidgetType.Group) painterResource(id = R.drawable.users_profiles_minus) else painterResource(id = R.drawable.calendar_02), contentDescription = null, tint = PRIMARY500)
                }

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = if (viewType == ReportWidgetType.Default) stringResource(id = R.string.day) else if (viewType == ReportWidgetType.Group) stringResource(id = R.string.number_of_members) else stringResource(id = R.string.days), style = fontRegular12(SECONDARY500))
                    Text(modifier = Modifier.padding(top = 2.dp),text = if (viewType == ReportWidgetType.Default) {if (secondText != -1) ctx.getString(R.string.days_ago, secondText.toString()) else ctx.getString(R.string.no_record)} else secondText.toString() , style = fontBold12(SECONDARY900))

                }
            }

        }
    }



}


@Preview
@Composable
private fun ReportWidgetPreview(){
    ReportWidget(viewType = ReportWidgetType.Default, firstText = 1200, secondText = 120)
}