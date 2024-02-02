package com.ntg.stepi.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ntg.stepi.R
import com.ntg.stepi.models.res.ADSRes
import com.ntg.stepi.util.extension.noRippleClickable
import com.ntg.stepi.util.extension.openInBrowser
import com.ntg.stepi.util.extension.timber

@Composable
fun AdsItem(
    modifier: Modifier = Modifier,
    ads: ADSRes? = ADSRes()
){

    if (ads == null) return

    val context = LocalContext.current
    Box(modifier = modifier
        .fillMaxSize()
        .aspectRatio(
            if (ads.position
                    .orEmpty()
                    .contains("-4")
            ) 4f else 2.25f
        )
        .clip(RoundedCornerShape(16.dp))
        .noRippleClickable {
            context.openInBrowser(ads.link.orEmpty())
        }) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(ads.banner)
                .size(coil.size.Size.ORIGINAL)
                .build()
        )

        if (painter.state is AsyncImagePainter.State.Success) {
            Image(
                painter = painter,
                contentDescription = "ads",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }else{
            Box(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colors.onBackground), contentAlignment = Alignment.Center){
                Icon(painter = painterResource(id = R.drawable.image), contentDescription = "", tint = MaterialTheme.colors.secondary)
            }
        }

    }

}