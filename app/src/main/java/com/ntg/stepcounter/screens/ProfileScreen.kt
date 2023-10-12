package com.ntg.stepcounter.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.DateItem
import com.ntg.stepcounter.components.ReportWidget
import com.ntg.stepcounter.components.SocialItem
import com.ntg.stepcounter.models.RGBColor
import com.ntg.stepcounter.models.components.ReportWidgetType
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.Background
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.PRIMARY900
import com.ntg.stepcounter.ui.theme.SECONDARY100
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.SECONDARY900
import com.ntg.stepcounter.ui.theme.fontBlack24
import com.ntg.stepcounter.ui.theme.fontMedium12
import com.ntg.stepcounter.ui.theme.fontMedium14
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.ui.theme.fontRegular14
import com.ntg.stepcounter.util.extension.daysUntilToday
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.vm.SocialNetworkViewModel
import com.ntg.stepcounter.vm.StepViewModel
import com.ntg.stepcounter.vm.UserDataViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    stepViewModel: StepViewModel,
    userDataViewModel: UserDataViewModel,
    socialNetworkViewModel: SocialNetworkViewModel
) {
    var aaa by remember { mutableFloatStateOf(0f) }
    var radius by remember { mutableFloatStateOf(32f) }
    var topOffset = with(LocalDensity.current) { aaa.toDp() }
    var contentHeight by remember { mutableFloatStateOf(0f) }
    var topBarColor by remember { mutableStateOf(RGBColor(252, 252, 255)) }
    val ctx = LocalContext.current

    val totalSteps = stepViewModel.getAllSteps().observeAsState().value
    val allDate = stepViewModel.getAllDate().observeAsState().value
    var dateSelected by remember { mutableStateOf("") }
    val status = userDataViewModel.getUserStatus().collectAsState(initial = "").value

    val isOpenToView = userDataViewModel.isShowReport().collectAsState(initial = true).value

    if (dateSelected.isEmpty() && allDate.orEmpty().isNotEmpty()) dateSelected =
        allDate.orEmpty()[0].date

    val socials = socialNetworkViewModel.getAll().observeAsState().value

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
                    elevation = 0.dp
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
                            }

                            if (allDate.orEmpty().isNotEmpty()) {
                                LazyRow(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 16.dp)
                                ) {
                                    itemsIndexed(
                                        allDate.orEmpty().distinctBy { it.date }) { index, it ->
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
                                        allDate.orEmpty().first { it.date == dateSelected }.count.orZero()
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
                                text = stringResource(id = R.string.your_achievment),
                                style = fontMedium14(SECONDARY500)
                            )
                            Text(
                                modifier = Modifier.padding(top = 8.dp, start = 16.dp),
                                text = stringResource(id = R.string.no_achievment),
                                style = fontRegular12(
                                    SECONDARY500
                                )
                            )
//                            val list = arrayListOf<Int>()
//                            for (i in 0..100)
//                                list.add(i)
//                            LazyColumn(modifier = Modifier.height(200.dp), content = {
//                                items(list){
//                                    Text(text = it.toString())
//                                }
//                            })

                            Divider(modifier = Modifier.height(16.dp), color = Background)
                        }
                    }


                    item {
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
                            Text(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .padding(vertical = 16.dp)
                                    .weight(1f),
                                text = stringResource(id = R.string.your_achievment),
                                style = fontRegular14(SECONDARY500)
                            )
                            CustomButton(
                                modifier = Modifier.padding(end = 8.dp),
                                text = stringResource(id = R.string.view),
                                style = ButtonStyle.TextOnly
                            )
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
//                            val list = arrayListOf<Int>()
//                            for (i in 0..100)
//                                list.add(i)
                            LazyColumn(
                                modifier = Modifier
                                    .height((socials?.size.orZero() * 37).dp)
                                    .padding(top = 8.dp), content = {
                                    items(socials.orEmpty()) { social ->
                                        SocialItem(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            id = social.id,
                                            title = social.name,
                                            itemClick = {
                                                navHostController.navigate(Screens.SocialScreen.name + "?id=$it")
                                            },
                                            edit = {
                                                navHostController.navigate(Screens.SocialScreen.name + "?id=$it")
                                            },
                                            delete = {
                                                socialNetworkViewModel.delete(social)
                                            }
                                        )
                                    }
                                })

                            CustomButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 24.dp),
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

//                    items(list){
//                        Text(modifier = Modifier.fillMaxWidth(), text = it.toString())
//                    }


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
                        Image(
                            modifier = Modifier.padding(start = 8.dp),
                            painter = painterResource(id = R.drawable.icons8_approval_2),
                            contentDescription = null
                        )
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
                                "STUDENT" -> {
                                    ctx.getString(R.string.student)
                                }

                                "Prof" -> {
                                    ctx.getString(R.string.prof)
                                }

                                else -> status
                            }
//                            when (status) {
//                                "STUDENT" -> {
//                                    ctx.getString(R.string.student_of, fieldStudy)
//                                }
//                                "Prof" -> {
//                                    ctx.getString(R.string.prof_of, fieldStudy)
//                                }
//                                else -> "$status $fieldStudy"
//                            }
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