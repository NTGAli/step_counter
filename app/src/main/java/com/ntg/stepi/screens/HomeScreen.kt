package com.ntg.stepi.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.ntg.stepi.BuildConfig
import com.ntg.stepi.R
import com.ntg.stepi.services.StepCounterService
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.components.AdsItem
import com.ntg.stepi.components.EmptyWidget
import com.ntg.stepi.components.ErrorMessage
import com.ntg.stepi.components.Loading
import com.ntg.stepi.components.Record
import com.ntg.stepi.components.ReportWidget
import com.ntg.stepi.components.TextAnimated
import com.ntg.stepi.components.Title
import com.ntg.stepi.models.ErrorStatus
import com.ntg.stepi.models.RGBColor
import com.ntg.stepi.models.components.ReportWidgetType
import com.ntg.stepi.models.res.SummariesRes
import com.ntg.stepi.nav.Screens
import com.ntg.stepi.services.StepWorker
import com.ntg.stepi.ui.theme.ERROR300
import com.ntg.stepi.ui.theme.fontBold12
import com.ntg.stepi.ui.theme.fontBold36
import com.ntg.stepi.ui.theme.fontMedium12
import com.ntg.stepi.ui.theme.fontMedium14
import com.ntg.stepi.ui.theme.fontRegular12
import com.ntg.stepi.util.extension.calculateRadius
import com.ntg.stepi.util.extension.checkInternet
import com.ntg.stepi.util.extension.daysUntilToday
import com.ntg.stepi.util.extension.foregroundServiceRunning
import com.ntg.stepi.util.extension.getColorComponentsForNumber
import com.ntg.stepi.util.extension.orZero
import com.ntg.stepi.util.extension.stepsToCalories
import com.ntg.stepi.util.extension.stepsToKilometers
import com.ntg.stepi.util.extension.timber
import com.ntg.stepi.vm.StepViewModel
import com.ntg.stepi.vm.UserDataViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    stepViewModel: StepViewModel,
    userDataViewModel: UserDataViewModel
) {
    val ctx = LocalContext.current
    startService(ctx)
    var topBarHeight by remember { mutableFloatStateOf(0f) }
    var radius by remember { mutableFloatStateOf(32f) }
    val backgroundColor = MaterialTheme.colors.onBackground
    var topBarColor by remember {
        mutableStateOf(
            RGBColor(
                backgroundColor.red.toInt(),
                backgroundColor.green.toInt(),
                backgroundColor.blue.toInt()
            )
        )
    }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    val topOffset = with(LocalDensity.current) { topBarHeight.toDp() }


    var internetConnection by remember {
        mutableStateOf(true)
    }

    var newUpdate by remember {
        mutableStateOf(false)
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




    var messagesID by remember {
        mutableStateOf(emptyList<String>())
    }

    claps = userDataViewModel.getClaps().collectAsState(initial = -1).value

    username = userDataViewModel.getUsername().collectAsState(initial = ".").value

    internetConnection = ctx.checkInternet()


//    val saver = object : Saver<BottomSheetState, Bundle> {
//
//        override fun restore(value: Bundle): BottomSheetState? {
//            return BottomSheetState(initialValue = BottomSheetValue.Expanded, density = Density(ctx))
//        }
//
//        override fun SaverScope.save(value: BottomSheetState): Bundle? {
//            val bundle = Bundle()
//            bundle.putFloat("currentValue", value.progress)
//            return bundle
//        }
//    }

//    val state =
//        rememberSaveable(saver = saver) {
//            BottomSheetState(
//                initialValue = BottomSheetValue.Expanded,
//                density = Density(ctx)
//            )
//        }



    BoxWithConstraints(modifier = Modifier.background(MaterialTheme.colors.onBackground)) {
        val sheetHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() - topOffset }
        val sheetPeekHeight =
            with(LocalDensity.current) { constraints.maxHeight.toDp() - contentHeight.toDp() - topOffset }







//        val stateValue by rememberSaveable {
//            mutableStateOf(scaffoldState.bottomSheetState.currentValue)
//        }


//        val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = state

        val scaffoldState = rememberBottomSheetScaffoldState(
//            bottomSheetState = state
        )

//        scaffoldState.bottomSheetState.

//        scaffoldState.bottomSheetState


        val animateRotation = remember { Animatable(0f) }
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(key1 = scaffoldState.bottomSheetState.isExpanded) {
            coroutineScope.launch {
                if (scaffoldState.bottomSheetState.isExpanded) {
                    animateRotation.animateTo(180f)
                } else {
                    animateRotation.animateTo(0f)
                }
//                state = scaffoldState.bottomSheetState
            }
        }

        val maxOffset =
            LocalDensity.current.run { (sheetHeight - sheetPeekHeight + topOffset).toPx() }
        val minOffset = LocalDensity.current.run { topOffset.toPx() }
        topBarColor = getColorComponentsForNumber(radius.toInt())


        BottomSheetScaffold(
            modifier = Modifier
                .background(MaterialTheme.colors.onBackground),
            sheetPeekHeight = sheetPeekHeight,
            topBar = {
                TopBar(
                    navHostController,
                    userDataViewModel,
                    topBarColor,
                    username,
                    claps,
                    newClaps,
                    messagesID,
                    newUpdate
                ) {
                    topBarHeight = it
                }
            },
            sheetContent = {
                Content(
                    navHostController,
                    userDataViewModel,
                    stepViewModel,
                    sheetHeight,
                    animateRotation,
                    newClaps = {
                        newClaps = it
                    },
                    messagesIds = {
                        messagesID = it.orEmpty()
                    },
                    update = {
                        newUpdate = it
                    }
                )
            },
            scaffoldState = scaffoldState,
            sheetElevation = radius.dp / 2,
            sheetShape = RoundedCornerShape(radius.dp, radius.dp, 0.dp, 0.dp),
            sheetBackgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.onBackground,
        ) {
            ReportItem(stepViewModel) {
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
    userDataViewModel: UserDataViewModel,
    topBarColor: RGBColor,
    username: String,
    claps: Int,
    newClaps: Int,
    messageId: List<String>,
    newUpdate: Boolean,
    layoutCoordinate: (Float) -> Unit
) {

    val uid = userDataViewModel.getUserId().collectAsState(initial = "").value


    TopAppBar(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                val height = layoutCoordinates.size.height
                layoutCoordinate.invoke(height.toFloat())
            },
        backgroundColor = Color(topBarColor.red, topBarColor.green, topBarColor.blue),
        title = {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = stringResource(id = R.string.app_name),
                    style = fontMedium14(
                        MaterialTheme.colors.primary
                    )
                )
                Icon(modifier = Modifier
                    .padding(start = 2.dp)
                    .size(16.dp), painter = painterResource(id = R.drawable.icons8_sneakers_1), contentDescription = null, tint = MaterialTheme.colors.primary)
            }


        },
        actions = {

            if (newUpdate) {
                IconButton(onClick = {
                    navHostController.navigate(Screens.UpdateScreen.name)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_down_square_contained),
                        contentDescription = null,
                        tint = MaterialTheme.colors.secondary
                    )
                }
            }

            IconButton(onClick = {
                navHostController.navigate(Screens.MessagesBoxScreen.name + "?uid=$uid")
            }) {
                Box {
                    Icon(
                        painter = painterResource(id = R.drawable.bell_02),
                        contentDescription = null,
                        tint = MaterialTheme.colors.secondary
                    )
                    if (messageId.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .drawBehind {
                                    drawCircle(
                                        color = ERROR300, radius = 24f
                                    )
                                }
                                .align(
                                    Alignment.TopStart
                                ),
                            text = messageId.size.toString(),
                            style = fontMedium12(
                                Color.White
                            )
                        )
                    }

                }

            }

            Box(modifier = Modifier.padding(end = 8.dp, start = 16.dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.primaryVariant)
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
                        style = fontRegular12(MaterialTheme.colors.primary)
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
    contentHeight: (Float) -> Unit,
) {

    val ctx = LocalContext.current
    val topRecord = stepViewModel.topRecord()?.observeAsState()?.value
    val userStepsToday = stepViewModel.getToday().observeAsState(initial = listOf()).value

    var stepsOfToday by remember {
        mutableIntStateOf(0)
    }
    var synced by remember {
        mutableIntStateOf(0)
    }

    stepsOfToday = 0
    synced = 0

    userStepsToday.forEach {
        if (it.count != 0 && it.count.orZero() >= it.start.orZero()) {
            stepsOfToday += it.count.orZero() - it.start.orZero()
            synced = it.synced.orZero()
        }
    }

    if (stepsOfToday + 55 > synced) {
        stepViewModel.clearSummaries()
    }


    Box(
        Modifier
            .onGloballyPositioned { layoutCoordinates ->
                val boxHeight = layoutCoordinates.size.height
                contentHeight.invoke(boxHeight.toFloat())
            })
    {


        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(MaterialTheme.colors.onBackground),
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


            TextAnimated(stepsOfToday) { digit ->
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 8.dp),
                    text = digit.digitChar.toString(),
                    style = fontBold36(MaterialTheme.colors.onPrimary)
                )
            }

            Text(
                modifier = Modifier.padding(bottom = 48.dp),
                text = stringResource(
                    id = R.string.km_cal,
                    ctx.stepsToKilometers(
                        stepsOfToday
                    ),
                    stepsToCalories(
                        stepsOfToday
                    )
                ),
                style = fontBold12(MaterialTheme.colors.secondary)
            )
        }
    }
}

private fun startService(context: Context) {
    timber("isInBackgroundStarted ")
    val serviceIntent = Intent(context, StepCounterService::class.java)
    if (!context.foregroundServiceRunning()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
            timber("isInBackgroundStarted ::: ForegroundService")
        } else {
            context.startService(serviceIntent)
            timber("isInBackgroundStarted ::: Service")
        }
        startServiceViaWorker(context)
    }
}

fun startServiceViaWorker(context: Context) {
    timber("startServiceViaWorker called")
    val UNIQUE_WORK_NAME = "StartMyServiceViaWorker"
    val workManager = WorkManager.getInstance(context)

    // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
    // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
    val request: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
        StepWorker::class.java,
        16,
        TimeUnit.MINUTES
    )
        .build()

    // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
    // do check for AutoStart permission
    workManager.enqueueUniquePeriodicWork(
        UNIQUE_WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        request
    )
}


@Composable
private fun Content(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel,
    stepViewModel: StepViewModel,
    sheetHeight: Dp,
    animateRotation: Animatable<Float, AnimationVector1D>,
    newClaps: (Int) -> Unit,
    messagesIds: (List<String>?) -> Unit,
    update: (Boolean) -> Unit = {},

    ) {
    val ctx = LocalContext.current
    var internetConnection = ctx.checkInternet()
    val owner = LocalLifecycleOwner.current
    val uid = userDataViewModel.getUserId().collectAsState(initial = "").value

    var loadData by remember {
        mutableStateOf(false)
    }
    var error by remember {
        mutableStateOf(false)
    }

    var tryAgain by remember {
        mutableStateOf(false)
    }
    var summaries by rememberSaveable {
        mutableStateOf<SummariesRes?>(null)
    }


    if (internetConnection && !loadData) {
        userDataViewModel.getUserId().collectAsState(initial = "").value.let { userId ->

            if (userId.isNotEmpty()) {

                LaunchedEffect(key1 = loadData, block = {

                    stepViewModel.summariesData(userId, summaries == null || tryAgain)
                        .observe(owner) {
                            when (it) {
                                is NetworkResult.Error -> {
                                    timber("SUMMARISE ::: ERR :: ${it.message}")
                                    error = true
                                    loadData = false
                                }

                                is NetworkResult.Loading -> {
                                    timber("SUMMARISE ::: Loading")
                                    loadData = true
                                }

                                is NetworkResult.Success -> {
                                    summaries = it.data?.data

                                    newClaps.invoke(it.data?.data?.claps ?: -1)
                                    if (it.data?.data?.messagesId.orEmpty().isNotEmpty()) {
                                        messagesIds.invoke(it.data?.data?.messagesId)
                                    }


                                    if (it.data?.data?.deadVersionCode != null) {
                                        userDataViewModel.setDeadCode(it.data.data.deadVersionCode.orZero())
                                    }


                                    if (it.data?.data?.versionCode != null) {
                                        update.invoke(it.data.data.versionCode > BuildConfig.VERSION_CODE)
                                    }

                                }
                            }
                        }

                })

            }
        }
    }


    val listState = rememberLazyListState()
// Remember a CoroutineScope to be able to launch
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit, block = {

        coroutineScope.launch {
            listState.animateScrollToItem(0)
        }
    })

    if (summaries == null) {
        Loading(isFull = false)
    } else if (!internetConnection) {
        ErrorMessage(
            modifier = Modifier.padding(top = 32.dp),
            status = ErrorStatus.Internet
        ) {
            internetConnection = true
            error = false
        }
    } else if (error) {
        tryAgain = false
        ErrorMessage(
            modifier = Modifier.padding(top = 32.dp),
            status = ErrorStatus.Failed
        ) {
            tryAgain = true
            error = false
        }
    } else {
        LazyColumn(modifier = Modifier
            .height(sheetHeight)
//            .background(MaterialTheme.colors.background)
            .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {

                item {
                    Icon(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .rotate(animateRotation.value),
                        painter = painterResource(id = R.drawable.chevron_up),
                        contentDescription = null,
                        tint = MaterialTheme.colors.secondary
                    )
                }

                item {
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colors.primaryVariant)
                            .padding(horizontal = 32.dp, vertical = 4.dp),
                        text = if (summaries?.rank != null) stringResource(
                            id = R.string.your_rank_format,
                            summaries?.rank.orEmpty()
                        ) else stringResource(id = R.string.no_record_format),
                        style = fontRegular12(MaterialTheme.colors.primary)
                    )
                }


                item {
                    Title(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 16.dp),
                        title = stringResource(id = R.string.challenge_monthly),
                        showBtn = true,
                        subText = summaries?.deadLineChallenge,
                        action = stringResource(
                            id = R.string.see_all
                        )
                    ) {
                        navHostController.navigate(Screens.DataChallengesScreen.name + "?uid=$uid")
                    }
                }

                if (summaries?.topMonth.orEmpty().isNotEmpty()) {
                    itemsIndexed(summaries?.topMonth.orEmpty()) { index, it ->
                        Record(
                            modifier = Modifier.padding(top = 8.dp),
                            uid = it.uid,
                            record = index,
                            title = it.title.orEmpty(),
                            steps = it.steps,
                            primaryBorder = true
                        ) {
                            navHostController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
                        }
                    }
                } else {
                    item {
                        EmptyWidget(title = ctx.getString(R.string.no_record_today))
                    }
                }

                item {
                    Title(
                        modifier = Modifier
                            .padding(bottom = 8.dp, top = 24.dp)
                            .padding(horizontal = 16.dp),
                        title = stringResource(id = R.string.top_today),
                        showBtn = summaries?.today?.size.orZero() == 3,
                        action = stringResource(
                            id = R.string.see_all
                        )
                    ) {
                        navHostController.navigate(Screens.SeeMoreScreen.name + "?type=TopToday")
                    }
                }

                if (summaries?.today.orEmpty().isNotEmpty()) {
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
                } else {
                    item {
                        EmptyWidget(title = ctx.getString(R.string.no_record_today))
                    }
                }

                item {
                    Title(
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 8.dp)
                            .padding(horizontal = 16.dp),
                        title = stringResource(id = R.string.top_rank_base_title),
                        showBtn = summaries?.fos?.size.orZero() == 3,
                        action = stringResource(
                            id = R.string.see_all
                        )
                    ) {
                        navHostController.navigate(Screens.SeeMoreScreen.name + "?type=TopBaseFos")
                    }
                }

                if (summaries?.fos.orEmpty().isNotEmpty()) {
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
                } else {
                    item {
                        EmptyWidget(title = ctx.getString(R.string.no_fos_record))
                    }
                }

                item {
                    AdsItem(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        ads = summaries?.ads?.first { it.position == "Home-4" })
                }

                item {
                    Title(
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 8.dp)
                            .padding(horizontal = 16.dp),
                        title = stringResource(id = R.string.top_rank_base_user),
                        showBtn = summaries?.all?.size.orZero() == 3,
                        action = stringResource(
                            id = R.string.see_all
                        )
                    ) {
                        navHostController.navigate(Screens.SeeMoreScreen.name + "?type=TopUsers")
                    }
                }

                if (summaries?.all.orEmpty().isNotEmpty()) {
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
                } else {
                    item {
                        EmptyWidget(title = ctx.getString(R.string.no_fos_record))
                    }
                }
//                try {
//                    item {
//
//                        AdsItem(
//                            modifier = Modifier
//                                .padding(top = 16.dp),
//                            ads = summaries?.ads?.first { it.position == "Home-0.44" } ?: ADSRes())
//
//                    }
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
                item {
                    Spacer(modifier = Modifier.padding(24.dp))
                }
            }
        , state = listState)
    }
}