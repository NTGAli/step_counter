package com.ntg.stepi.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ntg.stepi.models.components.Digit
import com.ntg.stepi.models.components.compareTo

@Composable
fun TextAnimated(
    count: Int,
    text: @Composable (Digit) -> Unit
) {


    Row(
        modifier = Modifier
            .animateContentSize()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        count.toString().reversed()
            .mapIndexed { index, c -> Digit(c, count, index) }
            .forEach { digit ->
                AnimatedContent(
                    targetState = digit,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { it } togetherWith slideOutVertically { -it }
                        } else {
                            slideInVertically { -it } togetherWith  slideOutVertically { it }
                        }
                    }, label = ""
                ) { digit ->
                   text.invoke(digit)
                }
            }
    }









//    val ccc = "$count "
//    val item = ccc.toString().reversed().split("")
//
//    LazyRow{
//
//        items(item){
//            AnimatedContent(
//                targetState = it,
//                transitionSpec = {
//                    slideIntoContainer(
//                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
//                        animationSpec = tween(durationMillis = 500)
//                    ) togetherWith
//                            slideOutOfContainer(
//                                towards = AnimatedContentTransitionScope.SlideDirection.Up,
//                                animationSpec = tween(durationMillis = 500)
//                            )
//                },
//                contentAlignment = Alignment.Center, label = it
//            ) { targetCount ->
//                Text(
//                    text = targetCount,
//                    fontSize = 36.sp
//                )
//            }
//        }
//
//    }

}

