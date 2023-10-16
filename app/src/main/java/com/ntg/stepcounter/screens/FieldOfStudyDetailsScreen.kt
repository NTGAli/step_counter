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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.DateItem
import com.ntg.stepcounter.components.Loading
import com.ntg.stepcounter.components.Record
import com.ntg.stepcounter.components.ReportWidget
import com.ntg.stepcounter.components.SocialView
import com.ntg.stepcounter.models.RGBColor
import com.ntg.stepcounter.models.components.ReportWidgetType
import com.ntg.stepcounter.models.res.SocialRes
import com.ntg.stepcounter.models.res.StepRes
import com.ntg.stepcounter.models.res.UserRes
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
import com.ntg.stepcounter.vm.UserDataViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FieldOfStudyDetailsScreen(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel,
    uid: String,
    rank: String
) {

    var totalSteps by remember {
        mutableIntStateOf(0)
    }

    var fosName by remember {
        mutableStateOf("")
    }

    var userBio by remember {
        mutableStateOf("")
    }

    var userId by remember {
        mutableStateOf("")
    }

    var userCount by remember {
        mutableIntStateOf(0)
    }


    var loading by remember {
        mutableStateOf(false)
    }

    var users by remember {
        mutableStateOf(listOf<UserRes>())
    }

    val ctx = LocalContext.current

    userDataViewModel.getUserId().collectAsState(initial = "").value.let {
        userId = it
    }

    if (fosName.isEmpty()){

        userDataViewModel.getFosDetails(uid).observe(LocalLifecycleOwner.current) {
            when (it) {
                is NetworkResult.Error -> {

                }

                is NetworkResult.Loading -> {
                    loading = true
                }

                is NetworkResult.Success -> {
                    totalSteps = it.data?.data?.totalSteps.orZero()
                    userCount = it.data?.data?.userCount.orZero()
                    fosName = it.data?.data?.title.orEmpty()
                    userBio = ctx.getString(
                        R.string.rank_formar,
                        rank
                    )
                    loading = false
                }
            }
        }


    }

    if (users.isEmpty()){
        userDataViewModel.userOfFos(uid).observe(LocalLifecycleOwner.current){
            when(it){
                is NetworkResult.Error -> {
                    timber("UsersOfFos ::: ERR")
                }
                is NetworkResult.Loading -> {
                    timber("UsersOfFos ::: Loading")
                }
                is NetworkResult.Success -> {
                    timber("UsersOfFos ::: ${it.data}")
                    users = it.data?.data.orEmpty()
                }
            }
        }
    }






    var aaa by remember { mutableFloatStateOf(0f) }
    var radius by remember { mutableFloatStateOf(32f) }
    val topOffset = with(LocalDensity.current) { aaa.toDp() }
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

                LazyColumn(modifier = Modifier
                    .height(sheetHeight)
                    .padding(horizontal = 16.dp)){
                    item {
                        Text(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp), text = stringResource(id = R.string.members), style = fontBold14(
                            SECONDARY500))
                    }

                    items(users){
                        Record(
                            modifier = Modifier.padding(bottom = 8.dp),
                            uid = it.uid,
                            record = it.rank.orZero()-1,
                            title = it.fullName.orEmpty(),
                            steps = it.steps.orZero(),
                            onClick = {}
                        )
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
                        viewType = ReportWidgetType.Group,
                        firstText = totalSteps.orZero(),
                        secondText = userCount
                    )


                    Row(
                        modifier = Modifier.padding(top = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fosName,
                            style = fontBlack24(PRIMARY900)
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

