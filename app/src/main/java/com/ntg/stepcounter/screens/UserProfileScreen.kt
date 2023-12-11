package com.ntg.stepcounter.screens

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.AchievementItem
import com.ntg.stepcounter.components.DateItem
import com.ntg.stepcounter.components.ErrorMessage
import com.ntg.stepcounter.components.Loading
import com.ntg.stepcounter.components.ReportWidget
import com.ntg.stepcounter.components.SingleColumnChartWithNegativeValues
import com.ntg.stepcounter.components.SocialView
import com.ntg.stepcounter.models.ErrorStatus
import com.ntg.stepcounter.models.RGBColor
import com.ntg.stepcounter.models.components.ReportWidgetType
import com.ntg.stepcounter.models.res.Achievement
import com.ntg.stepcounter.models.res.SocialRes
import com.ntg.stepcounter.models.res.StepRes
import com.ntg.stepcounter.ui.theme.Background
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.PRIMARY900
import com.ntg.stepcounter.ui.theme.SECONDARY100
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.SECONDARY900
import com.ntg.stepcounter.ui.theme.fontBlack24
import com.ntg.stepcounter.ui.theme.fontBold14
import com.ntg.stepcounter.ui.theme.fontMedium12
import com.ntg.stepcounter.ui.theme.fontMedium14
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.util.extension.calculateRadius
import com.ntg.stepcounter.util.extension.checkInternet
import com.ntg.stepcounter.util.extension.daysUntilToday
import com.ntg.stepcounter.util.extension.getColorComponentsForNumber
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.vm.UserDataViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserProfileScreen(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel,
    uid: String
) {

    var totalSteps by remember {
        mutableIntStateOf(0)
    }

    var userName by remember {
        mutableStateOf("")
    }

    var userBio by remember {
        mutableStateOf("")
    }

    var userId by remember {
        mutableStateOf("")
    }

    var userSate by remember {
        mutableStateOf("")
    }

    var totalDays by remember {
        mutableIntStateOf(0)
    }

    var totalClaps by remember {
        mutableIntStateOf(0)
    }

    var isVerified by remember {
        mutableStateOf(false)
    }

    var loading by remember {
        mutableStateOf(false)
    }

    val isLock = remember {
        mutableStateOf(false)
    }

    var isClap by remember {
        mutableStateOf(false)
    }

    var socials by remember {
        mutableStateOf(listOf<SocialRes>())
    }


    var error by remember {
        mutableStateOf(false)
    }

    var tryAgain by remember {
        mutableStateOf(false)
    }

    var internetConnection by remember {
        mutableStateOf(true)
    }

    var achievement by remember {
        mutableStateOf(Achievement())
    }

    val ctx = LocalContext.current


    internetConnection = ctx.checkInternet()

    userDataViewModel.getUserId().collectAsState(initial = "").value.let {
        userId = it
    }


    if (userName.isEmpty() && internetConnection || tryAgain) {

        userDataViewModel.getUserProfile(uid, userId).observe(LocalLifecycleOwner.current) {
            when (it) {
                is NetworkResult.Error -> {
                    error = true
                }

                is NetworkResult.Loading -> {
                    loading = true
                }

                is NetworkResult.Success -> {
                    totalSteps = it.data?.data?.steps.orZero()
                    totalDays = it.data?.data?.totalDays.orZero()
                    totalClaps = it.data?.data?.totalClaps.orZero()
                    userName = it.data?.data?.fullName.orEmpty()
                    isVerified = it.data?.data?.isVerified.orFalse()
                    isLock.value = it.data?.data?.isLock.orFalse()
                    isClap = it.data?.data?.isClap.orFalse()
                    socials = it.data?.data?.socials.orEmpty()
                    userSate = it.data?.data?.state.orEmpty()
                    val gradeId = it.data?.data?.gradeId.orZero()
                    achievement = it.data?.data?.achievement ?: Achievement()
                    userBio = when (userSate) {
                        "1" -> {
                            ctx.getString(
                                R.string.student_format,
                                when (gradeId) {
                                    1 -> ctx.getString(R.string.bachelor)
                                    2 -> ctx.getString(
                                        R.string.master
                                    )

                                    else -> ctx.getString(R.string.doctor)
                                },
                                it.data?.data?.fosName.orEmpty()
                            )
                        }

                        "2" -> {
                            ctx.getString(R.string.prof_of, it.data?.data?.fosName.orEmpty())
                        }

                        else -> ""
                    }
                    loading = false
                }
            }
        }


    }


    var appbarHeight by remember { mutableFloatStateOf(0f) }
    var radius by remember { mutableFloatStateOf(32f) }
    val topOffset = with(LocalDensity.current) { appbarHeight.toDp() }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    var topBarColor by remember { mutableStateOf(RGBColor(252, 252, 255)) }




    BoxWithConstraints {
        val sheetHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() - topOffset }
        val sheetPeekHeight =
            with(LocalDensity.current) { constraints.maxHeight.toDp() - contentHeight.toDp() - topOffset }
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val maxOffset =
            LocalDensity.current.run { (sheetHeight - sheetPeekHeight + topOffset).toPx() }
        val minOffset = LocalDensity.current.run { topOffset.toPx() }
        topBarColor = getColorComponentsForNumber(radius.toInt())
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




        BottomSheetScaffold(
            sheetPeekHeight = sheetPeekHeight,
            topBar = {
                TopBar(navHostController, topBarColor) {
                    appbarHeight = it
                }
            },
            sheetContent = {
                Content(
                    userDataViewModel,
                    uid,
                    achievement,
                    socials,
                    sheetHeight,
                    animateRotation,
                    isLock.value,
                    internetConnection,
                    tryAgain,
                )
            },
            scaffoldState = scaffoldState,
            sheetElevation = radius.dp / 2,
            sheetShape = RoundedCornerShape(radius.dp, radius.dp, 0.dp, 0.dp),
            floatingActionButton = {
                ClapButton(userDataViewModel, uid, isClap,userId, totalClaps)
            }
        ) {
            Box(
                Modifier
                    .background(Color.LightGray)
                    .onGloballyPositioned { layoutCoordinates ->
                        val b = layoutCoordinates.size.height
                        contentHeight = b.toFloat()
                    })
            {
                UserProfileData(totalSteps, totalDays, userName, isVerified, userSate, userBio)
            }
        }


        if (loading) {
            Loading()
        }

        if (!internetConnection) {
            ErrorMessage(modifier = Modifier.fillMaxHeight(), status = ErrorStatus.Internet) {
                internetConnection = true
                error = false
            }
        } else if (error) {
            ErrorMessage(modifier = Modifier.fillMaxHeight(), status = ErrorStatus.Failed) {
                tryAgain = true
                error = false
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
    appbarHeight: (Float) -> Unit
) {
    TopAppBar(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                val height = layoutCoordinates.size.height
                appbarHeight.invoke(height.toFloat())
            },
        backgroundColor = Color(topBarColor.red, topBarColor.blue, topBarColor.green),
        title = {
            Text(
                text = stringResource(id = R.string.profile),
                style = fontMedium14(
                    SECONDARY500
                )
            )
        },
        elevation = 0.dp,
        navigationIcon = {
            IconButton(onClick = {
                navHostController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ClapButton(
    userDataViewModel: UserDataViewModel,
    uid: String,
    isClap: Boolean,
    userId: String,
    totalClaps: Int,
){
    var isClapped by remember {
        mutableStateOf(isClap)
    }
    var countClaps by remember {
        mutableIntStateOf(totalClaps)
    }
    isClapped = isClap
    countClaps = totalClaps

    Box(
        modifier = Modifier, contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .wrapContentSize(),
            onClick = {
                if (!isClap && userId.isNotEmpty()) {
                    userDataViewModel.clap(userId, uid)
                    isClapped = true
                    countClaps += 1
                }
            },
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
            backgroundColor = if (isClapped) PRIMARY100 else MaterialTheme.colors.surface
        ) {

            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = countClaps.toString(),
                    style = fontBold14(if (isClapped) PRIMARY500 else SECONDARY500)
                )
                Image(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .padding(vertical = 8.dp),
                    painter = painterResource(id = R.drawable.claping),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun Content(
    userDataViewModel: UserDataViewModel,
    uid: String,
    achievement: Achievement,
    socials: List<SocialRes>,
    sheetHeight: Dp,
    animateRotation: Animatable<Float, AnimationVector1D>,
    isLock: Boolean,
    internetConnection: Boolean,
    tryAgain: Boolean,
) {
    val ctx = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val showChart = remember { mutableStateOf(true) }
    var loadData by remember {
        mutableStateOf(false)
    }
    var steps by remember {
        mutableStateOf(listOf<StepRes>())
    }

    var error by remember {
        mutableStateOf(false)
    }
    var dateSelected by remember { mutableStateOf("") }


    if (dateSelected.isEmpty() && steps.isNotEmpty()) dateSelected =
        steps[0].date

    val owner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = Unit, block = {
        if (!loadData && !isLock && internetConnection || tryAgain) {

            userDataViewModel.getUserSteps(uid).observe(owner) {
                when (it) {
                    is NetworkResult.Error -> {
                        loadData = false
                        error = true
                    }

                    is NetworkResult.Loading -> {
                        loadData = true
                    }

                    is NetworkResult.Success -> {
                        loadData = false
                        steps = it.data?.data.orEmpty()
                    }
                }
            }

        }
    })

    LazyColumn(
        modifier = Modifier
            .height(sheetHeight)
            .background(Color.White), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Icon(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .rotate(animateRotation.value),
                painter = painterResource(id = R.drawable.chevron_up),
                contentDescription = null
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = PRIMARY500,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(Background)
            )
            {

                Row(
                    modifier = Modifier.padding(top = 24.dp, start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.report_workout),
                        style = fontMedium14(
                            SECONDARY500
                        )
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isLock && steps.size != 1) {
                        IconButton(onClick = {
                            showChart.value = !showChart.value

                        }) {
                            Icon(
                                painter = painterResource(id = if (showChart.value) R.drawable.calendar_03 else R.drawable.bar_chart_square_03),
                                contentDescription = null
                            )
                        }
                    }

                }

                if (isLock) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = R.drawable.lock_02),
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                            text = stringResource(id = R.string.lock_to_view),
                            style = fontRegular12(
                                SECONDARY500
                            )
                        )

                    }

                } else if (steps.isNotEmpty()) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && steps.size != 1) {
                        if (showChart.value) {

                            val stepsMap: Map<LocalDate, Float> =
                                steps.associate { step ->
                                    val localDate = LocalDate.parse(step.date)
                                    val pair: Pair<LocalDate, Float> =
                                        localDate to step.steps.orZero().toFloat()
                                    pair
                                }
                            SingleColumnChartWithNegativeValues(
                                modifier = Modifier.padding(
                                    bottom = 24.dp
                                ), stepsMap
                            )
                        }

                    } else {
                        showChart.value = false
                    }

                    if (!showChart.value) {
                        LazyRow(
                            modifier = Modifier
                                .padding(top = 16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            itemsIndexed(
                                steps.distinctBy { it.date }) { index, it ->
                                DateItem(
                                    modifier = Modifier.padding(end = 8.dp),
                                    date = it.date,
                                    isSelected = if (dateSelected.isNotEmpty()) dateSelected == it.date else index == 0
                                ) { date ->
                                    dateSelected = date
                                }
                            }
                        }

                        Text(
                            modifier = Modifier.padding(top = 24.dp, start = 16.dp),
                            text = if (daysUntilToday(dateSelected) == 0L) {
                                stringResource(id = R.string.today)
                            } else if (daysUntilToday(dateSelected) == 1L) {
                                stringResource(id = R.string.yestrday)
                            } else {
                                stringResource(
                                    id = R.string.days_ago,
                                    daysUntilToday(dateSelected)
                                )
                            }, style = fontMedium12(SECONDARY500)
                        )

                        Text(
                            modifier = Modifier.padding(
                                top = 2.dp,
                                start = 16.dp,
                                bottom = 24.dp
                            ),
                            text = stringResource(
                                id = R.string.step_format,
                                steps
                                    .first { it.date == dateSelected }.steps.orZero()
                            ), style = fontMedium12(SECONDARY500)
                        )
                    }
                } else if (loadData) {

                    Loading(isFull = false)

                } else {
                    Text(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 24.dp,
                            start = 16.dp
                        ),
                        text = stringResource(id = R.string.no_record_yest),
                        style = fontMedium12(
                            SECONDARY500
                        )
                    )
                }


            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = SECONDARY100,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(Background)
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                    text = stringResource(id = R.string.achievments),
                    style = fontMedium14(SECONDARY500)
                )


                if (achievement.totalTop != 0 || achievement.is10.orFalse()) {

                    if (achievement.totalTop != 0) {
                        AchievementItem(
                            text = ctx.getString(
                                R.string.total_top_achievement,
                                achievement.totalTop.toString()
                            )
                        )
                    }

                    if (achievement.n3Days != 0) {
                        AchievementItem(
                            text = ctx.getString(
                                R.string.n3days_achievement,
                                achievement.n3Days.toString()
                            )
                        )
                    }

                    if (achievement.n7Days != 0) {
                        AchievementItem(
                            text = ctx.getString(
                                R.string.n7days_achievement,
                                achievement.n7Days.toString()
                            )
                        )
                    }

                    if (achievement.n30Days != 0) {
                        AchievementItem(
                            text = ctx.getString(
                                R.string.n30days_achievement,
                                achievement.n30Days.toString()
                            )
                        )
                    }

                    if (achievement.is10.orFalse()) {
                        AchievementItem(text = ctx.getString(R.string.n10_steps_achievement))
                    }

                    if (achievement.is20.orFalse()) {
                        AchievementItem(text = ctx.getString(R.string.n20_steps_achievement))
                    }

                    if (achievement.is30.orFalse()) {
                        AchievementItem(text = ctx.getString(R.string.n30_steps_achievement))
                    }

                    if (achievement.is40.orFalse()) {
                        AchievementItem(text = ctx.getString(R.string.n40_steps_achievement))
                    }

                    if (achievement.is50.orFalse()) {
                        AchievementItem(text = ctx.getString(R.string.n50_steps_achievement))
                    }


                } else {
                    Text(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 16.dp
                        ),
                        text = stringResource(id = R.string.no_achievment),
                        style = fontRegular12(
                            SECONDARY500
                        )
                    )

                }



                Divider(modifier = Modifier.height(16.dp), color = Background)
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = SECONDARY100,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(Background)
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                    text = stringResource(id = R.string.socilas_network),
                    style = fontMedium14(SECONDARY500)
                )

                if (socials.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.empty_social),
                        style = fontRegular12(
                            SECONDARY500
                        ),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyRow(modifier = Modifier.padding(16.dp)) {
                        items(socials) {
                            SocialView(
                                title = it.title.orEmpty(),
                                url = it.url.orEmpty(),
                                onClick = {
                                    uriHandler.openUri(it)
                                })
                        }
                    }
                }


            }
        }

    }
}


@Composable
fun LottieExample() {

    var isPlaying by remember {
        mutableStateOf(true)
    }
    var speed by remember {
        mutableFloatStateOf(1f)
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.clap_back)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false

    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier
                .size(164.dp)
        )
    }
}


@Composable
private fun UserProfileData(
    totalSteps: Int?,
    totalDays: Int,
    userName: String,
    isVerified: Boolean,
    userSate: String?,
    userBio: String
) {
    val ctx = LocalContext.current

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
            viewType = ReportWidgetType.Profile,
            firstText = totalSteps.orZero(),
            secondText = totalDays
        )


        Row(
            modifier = Modifier.padding(top = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userName,
                style = fontBlack24(PRIMARY900)
            )
            if (isVerified) {
                Image(
                    modifier = Modifier.padding(start = 8.dp),
                    painter = painterResource(id = R.drawable.icons8_approval_2),
                    contentDescription = null
                )
            }
        }


        if (userSate.orEmpty().isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PRIMARY100)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = userBio,
                    style = fontRegular12(SECONDARY900)
                )
            }
        }

    }

}

@Composable
fun ClapHand() {

    var isPlaying by remember {
        mutableStateOf(true)
    }
    var speed by remember {
        mutableFloatStateOf(1f)
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.clap)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false

    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier
        )
    }
}