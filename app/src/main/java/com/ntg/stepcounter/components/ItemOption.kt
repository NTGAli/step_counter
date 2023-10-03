package com.ntg.stepcounter.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.ui.theme.SECONDARY100
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.ui.theme.fontRegular14

@Composable
fun ItemOption(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    text: String,
    subText: String? = null,
    divider: Boolean = true,
    loading: MutableState<Boolean> = remember { mutableStateOf(false) },
    onClick: (String) -> Unit
) {

    val localDensity = LocalDensity.current

    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }



    Box(modifier = modifier) {

        if (loading.value) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(columnHeightDp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }


        Column(modifier = Modifier
            .onGloballyPositioned { coordinates ->
                columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
            }) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .fillMaxWidth()
                    .clickable {
                        onClick.invoke(text)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (painter != null) {
                    Spacer(modifier = Modifier.padding(start = 32.dp))
                    Icon(
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                            .padding(end = 8.dp)
                            .padding(vertical = 12.dp), painter = painter, contentDescription = "ic"
                    )
                }


                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                ) {

                    Text(
                        text = text,
                        style = fontRegular14(SECONDARY500)
                    )

                    if (subText != null) {
                        Text(
                            text = subText,
                            style = fontRegular12(SECONDARY500)
                        )
                    }
                }

            }



            if (divider) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (painter != null) 32.dp else 16.dp),
                    color = SECONDARY100
                )
            }

        }


    }

}