package com.ntg.stepcounter.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ntg.stepcounter.models.res.ADSRes
import com.ntg.stepcounter.util.extension.noRippleClickable
import com.ntg.stepcounter.util.extension.openInBrowser

@Composable
fun AdsItem(
    modifier: Modifier = Modifier,
    ads: ADSRes? = ADSRes()
){

    if (ads == null) return

    val context = LocalContext.current
    Box(modifier = modifier.fillMaxSize().aspectRatio(if (ads.position.orEmpty().contains("-4")) 4f else 2.25f).clip(RoundedCornerShape(16.dp)).noRippleClickable {
        context.openInBrowser(ads.link.orEmpty())
    }) {
        Image(modifier = Modifier.fillMaxSize(),painter = rememberAsyncImagePainter(ads.banner), contentDescription = "ads")
    }

}