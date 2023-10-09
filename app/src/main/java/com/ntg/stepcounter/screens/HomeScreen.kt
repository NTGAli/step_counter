package com.ntg.stepcounter.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ntg.stepcounter.FullSizeBlur
import com.ntg.stepcounter.R
import com.ntg.stepcounter.StepCounterListener
import com.ntg.stepcounter.components.ReportWidget
import com.ntg.stepcounter.models.RGBColor
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.models.components.ReportWidgetType
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.PRIMARY900
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontBlack24
import com.ntg.stepcounter.ui.theme.fontBold12
import com.ntg.stepcounter.ui.theme.fontBold24
import com.ntg.stepcounter.ui.theme.fontMedium14
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.util.extension.daysUntilToday
import com.ntg.stepcounter.util.extension.divideNumber
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.stepsToCalories
import com.ntg.stepcounter.util.extension.stepsToKilometers
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.vm.StepViewModel
import java.lang.Exception


//@OptIn(ExperimentalMaterial3Api::class)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navHostController: NavHostController, stepViewModel: StepViewModel) {

//    var setFakeDate by remember { mutableStateOf(true) }
//
//    if (setFakeDate){
//        fakeData(stepViewModel)
//        setFakeDate = false
//    }


    var aaa by remember { mutableFloatStateOf(0f) }
    var radius by remember { mutableFloatStateOf(32f) }
    var topBarColor by remember { mutableStateOf(RGBColor(252, 252, 255)) }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    var topOffset = with(LocalDensity.current) { aaa.toDp() }
    val ctx = LocalContext.current

    val stepsOfToday = stepViewModel.getToday().observeAsState().value?.size.orZero()
    val topRecord = stepViewModel.topRecord().observeAsState().value


    BoxWithConstraints {
        val sheetHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() - topOffset }
        val sheetPeekHeight =
            with(LocalDensity.current) { constraints.maxHeight.toDp() - contentHeight.toDp() - topOffset }
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()

        val maxOffset =
            LocalDensity.current.run { (sheetHeight - sheetPeekHeight + topOffset).toPx() }
        val minOffset = LocalDensity.current.run { topOffset.toPx() }

        topBarColor = getColorComponentsForNumber(radius.toInt())
        Log.d("dwdkj", "dwajdlkawd ${maxOffset} ---- ${minOffset}")




        BottomSheetScaffold(
            sheetPeekHeight = sheetPeekHeight,
            topBar = {
                TopAppBar(
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            val a = layoutCoordinates.size.height
                            aaa = a.toFloat()
                        },
                    backgroundColor = Color(topBarColor.red, topBarColor.blue, topBarColor.green),
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name_farsi),
                            style = fontMedium14(
                                SECONDARY500
                            )
                        )

                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(CircleShape)
                                .background(PRIMARY100)
                                .clickable {
                                    navHostController.navigate(Screens.ProfileScreen.name)
                                }
                                .size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "ع", style = fontRegular12(PRIMARY500))
                        }
                    },
                    elevation = 0.dp
                )
            },
            sheetContent = {

                val list = arrayListOf<Int>()
                for (i in 0..100)
                    list.add(i)
                LazyColumn(modifier = Modifier
                    .requiredHeight(sheetHeight)
                    .background(Color.White), content = {

                    items(list) {
                        Text(modifier = Modifier.fillMaxWidth(), text = it.toString())
                    }

                })
            },
            scaffoldState = scaffoldState,
            sheetElevation = radius.dp / 2,
            sheetShape = RoundedCornerShape(radius.dp, radius.dp, 0.dp, 0.dp)
        ) {
            Box(
                Modifier
                    .background(Color.LightGray)
                    .onGloballyPositioned { layoutCoordinates ->
                        val b = layoutCoordinates.size.height
                        contentHeight = b.toFloat()
                    })
            {


                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .background(Color(ctx.resources.getColor(R.color.background, null))),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ReportWidget(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .padding(top = 24.dp),
                        viewType = ReportWidgetType.Default,
                        firstText = topRecord?.record_count.orZero(),
                        secondText = if (topRecord?.date != null) daysUntilToday(topRecord.date).toInt() else -1
                    )


                    Text(
                        modifier = Modifier.padding(top = 32.dp),
                        text = divideNumber(
                            stepsOfToday
                        ),
                        style = fontBlack24(PRIMARY900)
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp, bottom = 48.dp),
                        text = stringResource(
                            id = R.string.km_cal,
                            stepsToKilometers(
                                stepsOfToday
                            ),
                            stepsToCalories(
                                stepsOfToday
                            )
                        ),
                        style = fontBold12(SECONDARY500)
                    )
                }
            }

        }


        try {
            radius = calculateRadius(
                minOffset,
                maxOffset,
                scaffoldState.bottomSheetState.requireOffset()
            )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }

}


fun calculateRadius(first: Float, end: Float, third: Float): Float {
    if (third > end) return 32f
    if (third < first) return 0f
    val range = end - first
    val normalizedThird = third - first
    return ((normalizedThird.toDouble() / range.toDouble()) * 32).toFloat()
}

fun getColorComponentsForNumber(number: Int): RGBColor {
    require(number in 0..32) { "Number must be between 0 and 32" }

    val startColor = RGBColor(255, 255, 255) // #FFFFFF
    val endColor = RGBColor(248, 248, 248)   // #FCFCFF

    val interpolatedRed = startColor.red + (endColor.red - startColor.red) * number / 32
    val interpolatedGreen = startColor.green + (endColor.green - startColor.green) * number / 32
    val interpolatedBlue = startColor.blue + (endColor.blue - startColor.blue) * number / 32

    return RGBColor(interpolatedRed, interpolatedGreen, interpolatedBlue)
}


private fun fakeData(stepViewModel: StepViewModel) {

//    val fakeDate = arrayListOf<Step>()
//
//
//    for (i in 0 until 6000)
//        fakeDate.add(Step(id = 0, timeUnix = null, date = "2023-10-2", inBackground = false))
//
//    fakeDate.forEach {
//        stepViewModel.insertManually(it)
//    }

//    val data = listOf(
//        Step(id = 0, timeUnix = null, date = "2023-10-12", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-12", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-12", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-12", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-12", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-13", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-13", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-13", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-15", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-15", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-16", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-17", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-18", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-18", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-18", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-18", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-18", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-18", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-18", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-18", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-18", inBackground = false),
//        Step(id = 0, timeUnix = null, date = "2023-10-19", inBackground = false),
//    )
//
//    data.forEach {
//        stepViewModel.insertManually(it)
//    }

}


