package com.ntg.stepcounter.screens

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.rememberBottomSheetScaffoldState
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.AchievementItem
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.DateItem
import com.ntg.stepcounter.components.ReportWidget
import com.ntg.stepcounter.components.SingleColumnChartWithNegativeValues
import com.ntg.stepcounter.components.SocialItem
import com.ntg.stepcounter.models.RGBColor
import com.ntg.stepcounter.models.Social
import com.ntg.stepcounter.models.components.ReportWidgetType
import com.ntg.stepcounter.models.res.Achievement
import com.ntg.stepcounter.models.res.UserRes
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.Background
import com.ntg.stepcounter.ui.theme.ERROR300
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.PRIMARY900
import com.ntg.stepcounter.ui.theme.SECONDARY100
import com.ntg.stepcounter.ui.theme.SECONDARY300
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.SECONDARY900
import com.ntg.stepcounter.ui.theme.fontBlack24
import com.ntg.stepcounter.ui.theme.fontMedium12
import com.ntg.stepcounter.ui.theme.fontMedium14
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.ui.theme.fontRegular14
import com.ntg.stepcounter.util.Constants
import com.ntg.stepcounter.util.extension.daysUntilToday
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.vm.SocialNetworkViewModel
import com.ntg.stepcounter.vm.StepViewModel
import com.ntg.stepcounter.vm.UserDataViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    stepViewModel: StepViewModel,
    userDataViewModel: UserDataViewModel,
    socialNetworkViewModel: SocialNetworkViewModel
) {


    var claps by remember { mutableStateOf(listOf<UserRes>()) }
    val loadClaps = remember { mutableStateOf(false) }
    var isVerified by rememberSaveable {
        mutableStateOf(false)
    }


    isVerified = userDataViewModel.isVerified().collectAsState(initial = false).value
    var layoutCoordinatePos by remember { mutableFloatStateOf(0f) }
    var radius by remember { mutableFloatStateOf(32f) }
    val topOffset = with(LocalDensity.current) { layoutCoordinatePos.toDp() }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    var topBarColor by remember { mutableStateOf(RGBColor(252, 252, 255)) }
    val ctx = LocalContext.current
    val observer = LocalLifecycleOwner.current

    val totalSteps = stepViewModel.getAllSteps().observeAsState().value

    val status = userDataViewModel.getUserStatus().collectAsState(initial = "").value

    val socials = socialNetworkViewModel.getAll().observeAsState().value

    val uid = userDataViewModel.getUserId().collectAsState(initial = null).value

    if (uid != null){
        LaunchedEffect(key1 = Unit, block = {
            userDataViewModel.clapsData(uid).observe(observer){
                when(it){
                    is NetworkResult.Error -> {
                        loadClaps.value = false
                    }
                    is NetworkResult.Loading -> {
                        loadClaps.value = true
                    }
                    is NetworkResult.Success -> {
                        claps = it.data?.data.orEmpty()
                        loadClaps.value = false
                    }
                }
            }
        })
    }


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

        LaunchedEffect(key1 = scaffoldState.bottomSheetState.isExpanded){
            coroutineScope.launch{
                if (scaffoldState.bottomSheetState.isExpanded){
                    animateRotation.animateTo(180f)
                }else{
                    animateRotation.animateTo(0f)
                }
            }
        }

        BottomSheetScaffold(
            sheetPeekHeight = sheetPeekHeight,
            topBar = {
                ProfileAppbar(navHostController, topBarColor){
                    layoutCoordinatePos = it
                }
            },
            sheetContent = {

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
                        UserDataSteps(userDataViewModel, stepViewModel)
                    }

                    item {
                       UserAchievements(userDataViewModel, stepViewModel)
                    }


                    item {
                        UserDataClap(
                            loadClaps,
                            claps,
                            userDataViewModel
                        ){
                            navHostController.navigate(Screens.UserClapsScreen.name)
                        }
                    }

                    item {
                        UserSocialData(socials,uid.orEmpty(), socialNetworkViewModel, navHostController)
                    }



                }
            },
            scaffoldState = scaffoldState,
            sheetElevation = radius.dp / 2,
            sheetShape = RoundedCornerShape(radius.dp, radius.dp, 0.dp, 0.dp),
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
                        secondText = stepViewModel.numberOfDate().observeAsState().value ?: -1
                    )


                    Row(
                        modifier = Modifier.padding(top = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = userDataViewModel.getUsername()
                                .collectAsState(initial = "").value,
                            style = fontBlack24(PRIMARY900)
                        )
                        if (isVerified){
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
                            text =
                            when (status) {
                                "1" -> {
                                    ctx.getString(R.string.student)
                                }

                                "2" -> {
                                    ctx.getString(R.string.prof)
                                }

                                else -> status
                            }
                            ,
                            style = fontRegular12(SECONDARY900)
                        )
                    }

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

@Composable
private fun UserDataClap(
    loadClaps: MutableState<Boolean>,
    claps: List<UserRes>,
    userDataViewModel: UserDataViewModel,
    onClick:() -> Unit
){

    var userClaps by remember {
        mutableIntStateOf(-1)
    }

    userClaps = userDataViewModel.getClaps().collectAsState(initial = -1).value

    timber("akjdlkawjdlkjawlkdjwalkd $userClaps")

    if (loadClaps.value){
        CircularProgressIndicator(modifier = Modifier
            .progressSemantics()
            .size(24.dp)
            .padding(vertical = 8.dp)
            , color = SECONDARY300, strokeWidth = 3.dp)
    }else if (claps.isNotEmpty()){
        Row(
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
                .background(Background),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (loadClaps.value){
                CircularProgressIndicator(modifier = Modifier
                    .progressSemantics()
                    .size(24.dp)
                    , color = SECONDARY500, strokeWidth = 3.dp)
            }

            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(vertical = 16.dp),
                text = stringResource(id = R.string.clpas_for_you_format, claps.size.toString()),
                style = fontRegular14(SECONDARY500)
            )

            if (claps.size > userClaps && userClaps != -1){
                Box(modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .size(6.dp)
                    .background(ERROR300))
            }

            Divider(modifier = Modifier.weight(1f), color = Background)

            CustomButton(
                modifier = Modifier.padding(end = 8.dp),
                text = stringResource(id = R.string.view),
                style = ButtonStyle.TextOnly
            ){
                userDataViewModel.setClaps(claps.size)
                onClick.invoke()
            }
        }
    }
}

@Composable
private fun UserSocialData(
    socials: List<Social>?,
    uid: String,
    socialNetworkViewModel: SocialNetworkViewModel,
    navHostController: NavHostController
){

    val uriHandler = LocalUriHandler.current
    val owner = LocalLifecycleOwner.current

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
            text = stringResource(id = R.string.your_social_media),
            style = fontMedium14(SECONDARY500)
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 16.dp),
            text = stringResource(id = R.string.socila_desc),
            style = fontRegular12(
                SECONDARY500
            )
        )

        LazyColumn(
            modifier = Modifier
                .height((socials?.size.orZero() * 33).dp)
                .padding(top = 8.dp), content = {
                items(socials.orEmpty()) { social ->
                    SocialItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        id = social.id,
                        title = social.name,
                        itemClick = {
                            uriHandler.openUri(social.pageId)
                        },
                        edit = {
                            navHostController.navigate(Screens.SocialScreen.name + "?id=$it")
                        },
                        delete = {

                            socialNetworkViewModel.deleteInServer(uid, it).observe(owner){
                                when(it){
                                    is NetworkResult.Error -> {

                                    }
                                    is NetworkResult.Loading -> {

                                    }
                                    is NetworkResult.Success -> {
                                        if (it.data?.isSuccess.orFalse()){
                                            socialNetworkViewModel.delete(social)
                                        }
                                    }
                                }
                            }

                        }
                    )
                }
            })

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            text = stringResource(id = R.string.add_new),
            style = if (socials.orEmpty()
                    .isEmpty()
            ) ButtonStyle.Contained else ButtonStyle.TextOnly
        ) {
            socialNetworkViewModel.socialNetworks.forEach {
                it.isSelected = false
            }
            navHostController.navigate(Screens.SocialScreen.name)
        }

    }
}


@Composable
private fun UserAchievements(userDataViewModel: UserDataViewModel, stepViewModel: StepViewModel){

    val context = LocalContext.current

    var achievement by remember {
        mutableStateOf(Achievement())
    }

    val steps = stepViewModel.getAllDate().observeAsState().value?.groupBy { it.date }



    userDataViewModel.getAchievement().collectAsState(initial = null).value.let {
        try {
            achievement = Gson().fromJson(it, Achievement::class.java)
        }catch (e: Exception){e.printStackTrace()}
    }

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
            text = stringResource(id = R.string.your_achievment),
            style = fontMedium14(SECONDARY500)
        )

        if (achievement.totalTop.orZero() == 0){
            Text(
                modifier = Modifier.padding(top = 8.dp, start = 16.dp),
                text = stringResource(id = R.string.no_achievment),
                style = fontRegular12(
                    SECONDARY500
                )
            )
        }else{

            AchievementItem(text = context.getString(R.string.total_top_achievement, achievement.totalTop.toString()))

            if (achievement.n3Days.orZero() != 0){
                AchievementItem(text = context.getString(R.string.n3days_achievement, achievement.n3Days.toString()))
            }

            if (achievement.n7Days.orZero() != 0){
                AchievementItem(text = context.getString(R.string.n7days_achievement, achievement.n7Days.toString()))
            }

            if (achievement.n30Days.orZero() != 0){
                AchievementItem(text = context.getString(R.string.n30days_achievement, achievement.n30Days.toString()))
            }

            if (steps.orEmpty().any { (_, dateSteps) ->
                    dateSteps.any { if(it.count > it.start.orZero()) (it.count - it.start.orZero()) > 10000 else false }
                }){
                AchievementItem(text = context.getString(R.string.n10_steps_achievement))
            }

            if (steps.orEmpty().any { (_, dateSteps) ->
                    dateSteps.any { if(it.count > it.start.orZero()) (it.count - it.start.orZero()) > 20000 else false }
                }){
                AchievementItem(text = context.getString(R.string.n20_steps_achievement))
            }

            if (steps.orEmpty().any { (_, dateSteps) ->
                    dateSteps.any { if(it.count > it.start.orZero()) (it.count - it.start.orZero()) > 30000 else false }
                }){
                AchievementItem(text = context.getString(R.string.n30_steps_achievement))
            }

            if (steps.orEmpty().any { (_, dateSteps) ->
                    dateSteps.any { if(it.count > it.start.orZero()) (it.count - it.start.orZero()) > 40000 else false }
                }){
                AchievementItem(text = context.getString(R.string.n40_steps_achievement))
            }

            if (steps.orEmpty().any { (_, dateSteps) ->
                    dateSteps.any { if(it.count > it.start.orZero()) (it.count - it.start.orZero()) > 50000 else false }
                }){
                AchievementItem(text = context.getString(R.string.n50_steps_achievement))
            }

        }


        Divider(modifier = Modifier.height(16.dp), color = Background)
    }
}

@Composable
private fun UserDataSteps(
    userDataViewModel: UserDataViewModel,
    stepViewModel: StepViewModel
){
    val isOpenToView = userDataViewModel.isShowReport().collectAsState(initial = true).value
    val allDate = stepViewModel.getAllDate().observeAsState().value
    var dateSelected by remember { mutableStateOf("") }
    val showChart = remember { mutableStateOf(true) }

    if (dateSelected.isEmpty() && allDate.orEmpty().isNotEmpty()) dateSelected =
        allDate?.lastOrNull()?.date.orEmpty()

    var countSelected by remember {
        mutableIntStateOf(0)
    }

    if (countSelected == 0){
        allDate?.filter { it.date == dateSelected }?.forEach {
            if (it.count != 0){
                countSelected += it.count - it.start.orZero()
            }
        }
    }



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
            if (!isOpenToView) {
                Icon(
                    modifier = Modifier.padding(start = 4.dp),
                    painter = painterResource(id = R.drawable.lock_02),
                    contentDescription = null
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && allDate?.groupBy { it.date }?.size != 1){
                IconButton(onClick = {
                    showChart.value =! showChart.value

                }) {
                    Icon(painter = painterResource(id = if (showChart.value) R.drawable.calendar_03 else R.drawable.bar_chart_square_03), contentDescription = null)
                }
            }

        }

        if (allDate.orEmpty().isNotEmpty()) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && allDate?.groupBy { it.date }?.size != 1){
                if (showChart.value){
                    val groupedSteps = allDate?.groupBy { it.date }
                    val result = groupedSteps?.mapKeys { LocalDate.parse(it.key) }
                        ?.mapValues { (_, steps) -> steps.filter { it.count.orZero() > it.start.orZero() && it.count.orZero() != 0 }.sumOf { it.count.orZero() - it.start.orZero() }.toFloat() }

                    SingleColumnChartWithNegativeValues(modifier = Modifier.padding(bottom = 24.dp),result)
                }

            }else{
                showChart.value = false
            }


            if (!showChart.value){

                LazyRow(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    itemsIndexed(
                        allDate.orEmpty().distinctBy { it.date }.sortedByDescending { it.date }) { index, step ->
                        DateItem(
                            modifier = Modifier.padding(end = 8.dp),
                            date = step.date,
                            isSelected = if (dateSelected.isNotEmpty()) dateSelected == step.date else index == 0
                        ) { date ->
                            countSelected = 0
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
                        countSelected
                    ), style = fontMedium12(SECONDARY500)
                )

            }

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



        //test chart





//        chartEntryModelProducer.setEntries(getRandomEntries())

















        // test chart

//        val groupedSteps = allDate?.groupBy { it.date }
//
//        val xValues = arrayListOf<Float>()
//        var xCount = 0f
//
//        groupedSteps?.forEach { (date, steps) ->
//
//            var nSteps = 0
//            steps.filter { it.start.orZero() < it.count.orZero() && it.count != 0 }.forEach {
//                nSteps += it.count.orZero() - it.start.orZero()
//            }
//
//            xValues.add(nSteps.toFloat())
//
//            xCount++
//        }
//
////        val xValues = groupedSteps?.map { it.value }
//
//
////        val chartEntryModel = entryModelOf(xValues)
//        val chartEntryModel = entryModelOf(*xValues.toTypedArray())
//
//        timber("kwajdlkawjlkdjwalkdj ::: $chartEntryModel -- $xValues")
//
//
//
//
//        Chart(
//            chart = lineChart(),
//            model = chartEntryModel,
////            startAxis = rememberStartAxis(),
////            bottomAxis = rememberBottomAxis(),
//        )










    }
}

@Composable
private fun ProfileAppbar(
    navHostController: NavHostController,
    topBarColor: RGBColor,
    layoutCoordinatePos:(Float) -> Unit
){
    TopAppBar(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                val a = layoutCoordinates.size.height
                layoutCoordinatePos.invoke(a.toFloat())
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
        actions = {
            IconButton(onClick = {
                navHostController.navigate(Screens.SettingsScreen.name)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = null
                )
            }
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