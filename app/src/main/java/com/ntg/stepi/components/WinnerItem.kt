package com.ntg.stepi.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Money
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.stepi.R
import com.ntg.stepi.ui.theme.fontBold14
import com.ntg.stepi.ui.theme.fontMedium12
import com.ntg.stepi.util.extension.divideNumber
import com.ntg.stepi.util.extension.noRippleClickable
import com.ntg.stepi.util.extension.stepsToKilometers
import com.ntg.stepi.util.extension.stepsToTime
import com.ntg.stepi.util.extension.toReadableTime
import kotlinx.coroutines.launch

@Composable
fun WinnerItem(
    modifier: Modifier = Modifier,
    uid: String,
    username: String,
    leagueName: String,
    steps: Int,
    days: Int,
    sponsor: String? = null,
    sponsorLink: String? = null,
    userClick: (String) -> Unit = {},
    sponsorClick: (String) -> Unit = {},
) {

    var visible by remember {
        mutableStateOf(false)
    }

    val ctx = LocalContext.current

    val animateRotation = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = visible) {
        coroutineScope.launch {
            if (visible) {
                animateRotation.animateTo(180f)
            } else {
                animateRotation.animateTo(0f)
            }
        }
    }

    Column(modifier = modifier
        .border(
            width = 1.dp,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.onSurface
        )
        .noRippleClickable {
            visible = !visible
        }) {

        Row {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier.clip(RoundedCornerShape(2.dp)).clickable {
                        userClick.invoke(uid)
                    }, text = username, style = fontBold14(MaterialTheme.colors.primary))
                    Icon(
                        modifier = Modifier.padding(start = 8.dp)
                            .rotate(animateRotation.value),
                        painter = painterResource(id = R.drawable.arrow_drop_down_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colors.secondary
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = leagueName,
                    style = fontMedium12(MaterialTheme.colors.secondary)
                )

            }


            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {

                Text(
                    text = stringResource(id = R.string.step_format, divideNumber(steps)),
                    style = fontMedium12(MaterialTheme.colors.secondary)
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = ctx.stepsToKilometers(steps),
                    style = fontMedium12(MaterialTheme.colors.secondary)
                )

            }


        }

        AnimatedVisibility(visible = visible) {

            Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                SubItem(
                    image = Icons.Rounded.AccessTime,
                    title = stringResource(id = R.string.time),
                    text = stepsToTime(steps).toReadableTime(ctx)
                )
                SubItem(
                    image = Icons.Rounded.Info,
                    title = stringResource(id = R.string.at),
                    text = stringResource(
                        id = R.string.days_format, days
                    )
                )
//                SubItem(
//                    image = Icons.Rounded.Money,
//                    title = stringResource(id = R.string.sponsor),
//                    text = sponsor ?: stringResource(
//                        id = R.string.un_known
//                    )
//                ) {
//                    if (sponsorLink != null) {
//                        sponsorClick.invoke(sponsorLink)
//                    }
//                }

                Spacer(modifier = Modifier.padding(8.dp))
            }


        }

    }


}