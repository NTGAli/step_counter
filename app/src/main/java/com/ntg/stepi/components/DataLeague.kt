package com.ntg.stepi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.stepi.R
import com.ntg.stepi.ui.theme.fontBlack14
import com.ntg.stepi.ui.theme.fontBold12
import com.ntg.stepi.ui.theme.fontRegular12

@Composable
fun DataLeague(
    modifier: Modifier,
    title: String,
    subTitle: String,
    first: String,
    second: String
) {

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colors.onSurface
            )
    ) {

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = fontBlack14(MaterialTheme.colors.onSecondary))
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = subTitle,
                style = fontRegular12(MaterialTheme.colors.secondary)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colors.background)
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colors.onSurface
                )
        ) {

            Row(modifier = Modifier.height(IntrinsicSize.Min)) {

                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = first, style = fontBold12(MaterialTheme.colors.primary))
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = stringResource(id = R.string.time_left),
                        style = fontRegular12(MaterialTheme.colors.secondary)
                    )
                }


                Divider(
                    modifier = Modifier.fillMaxHeight().width(1.dp),
                    color = MaterialTheme.colors.onSurface,
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = second, style = fontBold12(MaterialTheme.colors.primary))
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = stringResource(id = R.string.your_rank),
                        style = fontRegular12(MaterialTheme.colors.secondary)
                    )
                }

            }


        }

    }

}