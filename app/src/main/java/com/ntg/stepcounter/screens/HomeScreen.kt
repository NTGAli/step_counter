package com.ntg.stepcounter.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ntg.stepcounter.R
import com.ntg.stepcounter.StepCounterListener
import com.ntg.stepcounter.vm.StepViewModel
import java.lang.Exception


//@OptIn(ExperimentalMaterial3Api::class)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen() {

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Bottom sheet scaffold")
            })
        },

        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(128.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Swipe up to expand sheet")
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sheet content")
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
//                        scope.launch { scaffoldState.bottomSheetState.hide() }
                    }
                ) {
                    Text("Click to collapse sheet")
                    Divider(Modifier.width(500.dp))
                }
            }
        },
        scaffoldState = scaffoldState,
//        floatingActionButton = {
//            var clickCount by remember { mutableStateOf(0) }
//            FloatingActionButton(
//                onClick = {
//                    // show snackbar as a suspend function
//                    scope.launch {
//                        scaffoldState.snackbarHostState.showSnackbar("Snackbar #${++clickCount}")
//                    }
//                }
//            ) {
//                Icon(Icons.Default.Favorite, contentDescription = "Localized description")
//            }
//        }
//        ,
//        floatingActionButtonPosition = FabPosition.End,
        sheetPeekHeight = 500.dp
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(100) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Gray)
                )
            }
        }
    }


}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetTest(stepViewModel: StepViewModel) {

    var steps by remember { mutableStateOf("") }


    StepCounterListener {
        if (it != null)
            stepViewModel.insertStep()
        Log.d("dwd","wdlwkjkdlkwjdlkwad 2222")
        try {
            Log.d("dwd","wdlwkjkdlkwjdlkwad 333 ${it!!.values[0]}")
            steps = it!!.values[0].toString()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }.setup(LocalContext.current)


    var aaa by remember { mutableFloatStateOf(0f) }
    var radius by remember { mutableFloatStateOf(32f) }
    var topBarColor by remember { mutableStateOf(RGBColor(252, 252, 255)) }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    var topOffset = with(LocalDensity.current) { aaa.toDp() }
    val density = LocalDensity.current
    val ctx = LocalContext.current


    BoxWithConstraints() {
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
                TopAppBar(modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val a = layoutCoordinates.size.height
                        Log.d("DWD", "awdawdkjlwakdwdw $a")
                        aaa = a.toFloat()
                    },
                    backgroundColor = Color(topBarColor.red, topBarColor.blue, topBarColor.green),
                    title = {
                        Text(text = "Wassertemperaturen", fontSize = 20.sp)

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
                        Log.d("wadaw", "wadwadwadwadwdwd $b --- $contentHeight")
                    }) {


                Box(
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth()
                        .background(Color(ctx.resources.getColor(R.color.background, null)))
                ) {
                    Text(text = "steps : ${stepViewModel.getAll().observeAsState().value?.filter { it.inBackground }?.size}")
                }
            }
        }


        try {
            radius = calculateRadius(
                minOffset,
                maxOffset,
                scaffoldState.bottomSheetState.requireOffset()
            )
//            Log.d("SHEEET", "awkldkawdklawjd:::${scaffoldState.bottomSheetState.requireOffset().dp.value} ")
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }


}

private fun calculateRadius(first: Float, end: Float, third: Float): Float {
    if (third > end) return 32f
    if (third < first) return 0f
    val range = end - first
    val normalizedThird = third - first
    return ((normalizedThird.toDouble() / range.toDouble()) * 32).toFloat()
}

private fun getColorForNumber(number: Int): Triple<Int, Int, Int> {
    require(number in 0..32) { "Number must be between 0 and 32" }

    val startColor = 0xFFFFFF // #FFFFFF in decimal
    val endColor = 0xFCFCFF // #FCFCFF in decimal

    val redStart = (startColor shr 16) and 0xFF
    val greenStart = (startColor shr 8) and 0xFF
    val blueStart = startColor and 0xFF

    val redEnd = (endColor shr 16) and 0xFF
    val greenEnd = (endColor shr 8) and 0xFF
    val blueEnd = endColor and 0xFF

    val interpolatedRed = (redStart + (redEnd - redStart) * number / 32).toInt()
    val interpolatedGreen = (greenStart + (greenEnd - greenStart) * number / 32).toInt()
    val interpolatedBlue = (blueStart + (blueEnd - blueStart) * number / 32).toInt()

    val interpolatedColor =
        (interpolatedRed shl 16) or (interpolatedGreen shl 8) or interpolatedBlue

//    return "#" + Integer.toHexString(interpolatedColor).padStart(6, '0').toUpperCase()
    return Triple(interpolatedRed, interpolatedGreen, interpolatedBlue)
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

private fun loadData(context: Context) {

    // In this function we will retrieve data
    val sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
    val savedNumber = sharedPreferences.getFloat("key1", 0f)

    // Log.d is used for debugging purposes
    Log.d("MainActivity", "$savedNumber")

}

data class RGBColor(val red: Int, val green: Int, val blue: Int)
