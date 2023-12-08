package com.ntg.stepcounter.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.EmptyWidget
import com.ntg.stepcounter.components.ErrorMessage
import com.ntg.stepcounter.components.Loading
import com.ntg.stepcounter.components.Record
import com.ntg.stepcounter.components.ReportWidget
import com.ntg.stepcounter.components.Title
import com.ntg.stepcounter.models.ErrorStatus
import com.ntg.stepcounter.models.RGBColor
import com.ntg.stepcounter.models.components.ReportWidgetType
import com.ntg.stepcounter.models.res.SummariesRes
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.ERROR300
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.PRIMARY900
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontBlack24
import com.ntg.stepcounter.ui.theme.fontBold12
import com.ntg.stepcounter.ui.theme.fontMedium14
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.util.extension.calculateRadius
import com.ntg.stepcounter.util.extension.checkInternet
import com.ntg.stepcounter.util.extension.daysUntilToday
import com.ntg.stepcounter.util.extension.divideNumber
import com.ntg.stepcounter.util.extension.getColorComponentsForNumber
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.stepsToCalories
import com.ntg.stepcounter.util.extension.stepsToKilometers
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.vm.StepViewModel
import com.ntg.stepcounter.vm.UserDataViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    stepViewModel: StepViewModel,
    userDataViewModel: UserDataViewModel
) {

    var topBarHeight by remember { mutableFloatStateOf(0f) }
    var radius by remember { mutableFloatStateOf(32f) }
    var topBarColor by remember { mutableStateOf(RGBColor(252, 252, 255)) }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    val topOffset = with(LocalDensity.current) { topBarHeight.toDp() }


    var internetConnection by remember {
        mutableStateOf(true)
    }

    var username by remember {
        mutableStateOf(".")
    }


    var claps by remember {
        mutableIntStateOf(-1)
    }

    var newClaps by remember {
        mutableIntStateOf(-1)
    }

    claps = userDataViewModel.getClaps().collectAsState(initial = -1).value
    val ctx = LocalContext.current

    username = userDataViewModel.getUsername().collectAsState(initial = ".").value

    internetConnection = ctx.checkInternet()




    BoxWithConstraints {
        val sheetHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() - topOffset }
        val sheetPeekHeight =
            with(LocalDensity.current) { constraints.maxHeight.toDp() - contentHeight.toDp() - topOffset }
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val animateRotation = remember { Animatable(0f) }
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(key1 = scaffoldState.bottomSheetState.isExpanded) {
            coroutineScope.launch {
                if (scaffoldState.bottomSheetState.isExpanded) {
                    animateRotation.animateTo(180f)
                } else {
                    animateRotation.animateTo(0f)
                }
            }
        }

        val maxOffset =
            LocalDensity.current.run { (sheetHeight - sheetPeekHeight + topOffset).toPx() }
        val minOffset = LocalDensity.current.run { topOffset.toPx() }
        topBarColor = getColorComponentsForNumber(radius.toInt())




        BottomSheetScaffold(
            sheetPeekHeight = sheetPeekHeight,
            topBar = {
                TopBar(navHostController, topBarColor, username, claps, newClaps){
                    topBarHeight = it
                }
            },
            sheetContent = {
                Content(
                    navHostController,
                    userDataViewModel,
                    stepViewModel,
                    sheetHeight,
                    animateRotation
                ){
                    newClaps = it
                }
            },
            scaffoldState = scaffoldState,
            sheetElevation = radius.dp / 2,
            sheetShape = RoundedCornerShape(radius.dp, radius.dp, 0.dp, 0.dp)
        ) {
            ReportItem(stepViewModel){
                contentHeight = it
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

@Composable
private fun TopBar(
    navHostController: NavHostController,
    topBarColor: RGBColor,
    username: String,
    claps: Int,
    newClaps: Int,
    layoutCoordinate:(Float) -> Unit
) {
    TopAppBar(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                val height = layoutCoordinates.size.height
                layoutCoordinate.invoke(height.toFloat())
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

            Box(modifier = Modifier.padding(end = 8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PRIMARY100)
                        .clickable {
                            navHostController.navigate(Screens.ProfileScreen.name)
                        }
                        .size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = try {
                            username[0].toString()
                        } catch (e: Exception) {
                            "ش"
                        },
                        style = fontRegular12(PRIMARY500)
                    )
                }

                if (claps != -1 && newClaps.orZero() != -1 && claps < newClaps.orZero()) {
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clip(CircleShape)
                            .size(6.dp)
                            .background(ERROR300)
                            .align(
                                Alignment.TopStart
                            )
                    )
                }
            }
        },
        elevation = 0.dp
    )
}


@Composable
private fun ReportItem(
    stepViewModel: StepViewModel,
    contentHeight: (Float) -> Unit
){
    val topRecord = stepViewModel.topRecord()?.observeAsState()?.value
    val ctx = LocalContext.current
    val userStepsToday = stepViewModel.getToday().observeAsState().value
    var stepsOfToday = 0


    userStepsToday?.forEach {
        if (it.count != 0 && it.count.orZero() >= it.start.orZero()) {
            stepsOfToday += it.count.orZero() - it.start.orZero()
        }
    }
    Box(
        Modifier
            .background(Color.LightGray)
            .onGloballyPositioned { layoutCoordinates ->
                val boxHeight = layoutCoordinates.size.height
                contentHeight.invoke(boxHeight.toFloat())
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
                modifier = Modifier
                    .padding(top = 32.dp),
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


@Composable
private fun Content(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel,
    stepViewModel: StepViewModel,
    sheetHeight: Dp,
    animateRotation: Animatable<Float, AnimationVector1D>,
    newClaps:(Int) -> Unit
) {
    val ctx = LocalContext.current
    var internetConnection = ctx.checkInternet()
    val owner = LocalLifecycleOwner.current


    var loadData by remember {
        mutableStateOf(false)
    }
    var error by remember {
        mutableStateOf(false)
    }

    var tryAgain by remember {
        mutableStateOf(false)
    }
    var summaries by remember {
        mutableStateOf<SummariesRes?>(null)
    }
    if (internetConnection && !loadData) {
        userDataViewModel.getUserId().collectAsState(initial = "").value.let { userId ->


            if (userId.isNotEmpty()) {

                LaunchedEffect(key1 = Unit, block = {

                    stepViewModel.summariesData(userId, summaries == null || tryAgain)
                        .observe(owner) {
                            when (it) {
                                is NetworkResult.Error -> {
                                    timber("SUMMARISE ::: ERR :: ${it.message}")
                                    error = true
                                    loadData = true
                                }

                                is NetworkResult.Loading -> {
                                    timber("SUMMARISE ::: Loading")
                                    loadData = true
                                }

                                is NetworkResult.Success -> {
                                    summaries = it.data?.data
                                    newClaps.invoke(it.data?.data?.claps ?: -1)
                                    loadData = false
                                }
                            }
                        }

                })

            }
        }
    }


    if (loadData) {
        Loading(isFull = false)
    }
    else if (!internetConnection) {
        ErrorMessage(
            modifier = Modifier.padding(top = 32.dp),
            status = ErrorStatus.Internet
        ) {
            internetConnection = true
            error = false
        }
    }
    else if (error) {
        tryAgain = false
        ErrorMessage(
            modifier = Modifier.padding(top = 32.dp),
            status = ErrorStatus.Failed
        ) {
            tryAgain = true
            error = false
        }
    }
    else {
        Column(
            modifier = Modifier
                .height(sheetHeight)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {


            Icon(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .rotate(animateRotation.value),
                painter = painterResource(id = R.drawable.chevron_up),
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PRIMARY100)
                    .padding(horizontal = 32.dp, vertical = 4.dp),
                text = if (summaries?.rank != null) stringResource(
                    id = R.string.your_rank_format,
                    summaries?.rank.orEmpty()
                ) else stringResource(id = R.string.no_record_format),
                style = fontRegular12(PRIMARY900)
            )

            Title(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 16.dp),
                title = stringResource(id = R.string.top_today),
                action = stringResource(
                    id = R.string.see_all
                )
            ) {
                navHostController.navigate(Screens.SeeMoreScreen.name + "?type=TopToday")
            }
            if (summaries?.today.orEmpty().isNotEmpty()) {
                LazyColumn(content = {
                    itemsIndexed(summaries?.today.orEmpty()) { index, it ->
                        Record(
                            modifier = Modifier.padding(top = 8.dp),
                            uid = it.uid,
                            record = index,
                            title = it.title.orEmpty(),
                            steps = it.steps
                        ) {
                            navHostController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
                        }
                    }
                })
            } else {
                EmptyWidget(title = ctx.getString(R.string.no_record_today))
            }


            Title(
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 8.dp)
                    .padding(horizontal = 16.dp),
                title = stringResource(id = R.string.top_rank_base_fos),
                action = stringResource(
                    id = R.string.see_all
                )
            ) {
                navHostController.navigate(Screens.SeeMoreScreen.name + "?type=TopBaseFos")
            }

            if (summaries?.fos.orEmpty().isNotEmpty()) {
                LazyColumn(content = {
                    itemsIndexed(summaries?.fos.orEmpty()) { index, it ->
                        Record(
                            modifier = Modifier.padding(top = 8.dp),
                            uid = it.uid,
                            record = index,
                            title = it.title.orEmpty(),
                            steps = it.steps
                        ) {
                            navHostController.navigate(Screens.FieldOfStudyDetailsScreen.name + "?uid=$it&rank=${index + 1}")
                        }
                    }
                })
            } else {
                EmptyWidget(title = ctx.getString(R.string.no_fos_record))
            }

            Title(
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 8.dp)
                    .padding(horizontal = 16.dp),
                title = stringResource(id = R.string.top_rank_base_user),
                action = stringResource(
                    id = R.string.see_all
                )
            ) {
                navHostController.navigate(Screens.SeeMoreScreen.name + "?type=TopUsers")
            }

            if (summaries?.all.orEmpty().isNotEmpty()) {
                LazyColumn(modifier = Modifier.padding(bottom = 64.dp), content = {
                    itemsIndexed(summaries?.all.orEmpty()) { index, it ->
                        Record(
                            modifier = Modifier.padding(top = 8.dp),
                            uid = it.uid,
                            record = index,
                            title = it.title.orEmpty(),
                            steps = it.steps
                        ) {
                            navHostController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
                        }
                    }
                })
            } else {
                EmptyWidget(title = ctx.getString(R.string.no_fos_record))
            }
        }
    }
}