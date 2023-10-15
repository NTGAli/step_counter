package com.ntg.stepcounter.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.DateItem
import com.ntg.stepcounter.components.Loading
import com.ntg.stepcounter.components.ReportWidget
import com.ntg.stepcounter.components.SocialView
import com.ntg.stepcounter.models.RGBColor
import com.ntg.stepcounter.models.components.ReportWidgetType
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
import com.ntg.stepcounter.util.extension.daysUntilToday
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.vm.SocialNetworkViewModel
import com.ntg.stepcounter.vm.StepViewModel
import com.ntg.stepcounter.vm.UserDataViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserProfileScreen(
    navHostController: NavHostController,
    stepViewModel: StepViewModel,
    userDataViewModel: UserDataViewModel,
    socialNetworkViewModel: SocialNetworkViewModel,
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

    var steps by remember {
        mutableStateOf(listOf<StepRes>())
    }

    val ctx = LocalContext.current

    userDataViewModel.getUserId().collectAsState(initial = "").value.let {
        userId = it
    }

    if (userName.isEmpty()){

        userDataViewModel.getUserProfile(uid, userId).observe(LocalLifecycleOwner.current) {
            when (it) {
                is NetworkResult.Error -> {

                }

                is NetworkResult.Loading -> {
                    loading = true
                }

                is NetworkResult.Success -> {
                    timber("akwdjkalwjdklwajdlkawjdlkwjd")
                    totalSteps = it.data?.data?.steps.orZero()
                    totalDays = it.data?.data?.totalDays.orZero()
                    totalClaps = it.data?.data?.totalClaps.orZero()
                    userName = it.data?.data?.fullName.orEmpty()
                    isVerified = it.data?.data?.isVerified.orFalse()
                    isLock.value = it.data?.data?.isLock.orFalse()
                    isClap = it.data?.data?.isClap.orFalse()
                    socials = it.data?.data?.socials.orEmpty()
                    val gradeId = it.data?.data?.gradeId.orZero()
                    userBio = ctx.getString(
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
                    loading = false
                }
            }
        }


    }

    if (!isLock.value) {

        userDataViewModel.getUserSteps(uid).observe(LocalLifecycleOwner.current) {
            when (it) {
                is NetworkResult.Error -> {

                }

                is NetworkResult.Loading -> {
                }

                is NetworkResult.Success -> {
                    steps = it.data?.data.orEmpty()
                }
            }
        }

    }


    var aaa by remember { mutableFloatStateOf(0f) }
    var radius by remember { mutableFloatStateOf(32f) }
    val topOffset = with(LocalDensity.current) { aaa.toDp() }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    var topBarColor by remember { mutableStateOf(RGBColor(252, 252, 255)) }

    var dateSelected by remember { mutableStateOf("") }


    if (dateSelected.isEmpty() && steps.isNotEmpty()) dateSelected =
        steps[0].date


    BoxWithConstraints {
        val sheetHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() - topOffset }
        val sheetPeekHeight =
            with(LocalDensity.current) { constraints.maxHeight.toDp() - contentHeight.toDp() - topOffset }
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val maxOffset =
            LocalDensity.current.run { (sheetHeight - sheetPeekHeight + topOffset).toPx() }
        val minOffset = LocalDensity.current.run { topOffset.toPx() }
        topBarColor = getColorComponentsForNumber(radius.toInt())





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
            },
            sheetContent = {

                LazyColumn(
                    modifier = Modifier
                        .height(sheetHeight)
                        .background(Color.White), horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    item {
                        Icon(
                            modifier = Modifier.padding(top = 8.dp),
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

                            Text(
                                modifier = Modifier.padding(top = 24.dp, start = 16.dp),
                                text = stringResource(id = R.string.report_workout),
                                style = fontMedium14(
                                    SECONDARY500
                                )
                            )

                            if (isLock.value) {

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
                                LazyRow(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 16.dp)
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
                            Text(
                                modifier = Modifier.padding(top = 8.dp, start = 16.dp),
                                text = stringResource(id = R.string.no_achievment),
                                style = fontRegular12(
                                    SECONDARY500
                                )
                            )

                            Divider(modifier = Modifier.height(16.dp), color = Background)
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
                                            onClick = {})
                                    }
                                }
                            }


                        }
                    }

                }
            },
            scaffoldState = scaffoldState,
            sheetElevation = radius.dp / 2,
            sheetShape = RoundedCornerShape(radius.dp, radius.dp, 0.dp, 0.dp),
            floatingActionButton = {
                Box(
                    modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = 24.dp)
                    , contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .wrapContentSize(),
                        onClick = {
                            if (!isClap && userId.isNotEmpty()) {
                                userDataViewModel.clap(userId, uid)
                                isClap = true
                                totalClaps+=1
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        elevation = 4.dp,
                        backgroundColor = if (isClap) PRIMARY100 else MaterialTheme.colors.surface
                    ) {

                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = totalClaps.toString(),
                                style = fontBold14(if (isClap) PRIMARY500 else SECONDARY500)
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


        if (loading){
            Loading()
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