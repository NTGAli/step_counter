package com.ntg.stepcounter.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.ErrorMessage
import com.ntg.stepcounter.components.Loading
import com.ntg.stepcounter.components.Record
import com.ntg.stepcounter.components.ReportWidget
import com.ntg.stepcounter.models.ErrorStatus
import com.ntg.stepcounter.models.RGBColor
import com.ntg.stepcounter.models.components.ReportWidgetType
import com.ntg.stepcounter.models.res.UserRes
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.Background
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY900
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.SECONDARY900
import com.ntg.stepcounter.ui.theme.fontBlack24
import com.ntg.stepcounter.ui.theme.fontBold14
import com.ntg.stepcounter.ui.theme.fontMedium14
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.util.extension.calculateRadius
import com.ntg.stepcounter.util.extension.checkInternet
import com.ntg.stepcounter.util.extension.getColorComponentsForNumber
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.vm.UserDataViewModel
import kotlinx.coroutines.launch

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

    var error by remember {
        mutableStateOf(false)
    }

    var tryAgain by remember {
        mutableStateOf(false)
    }

    var internetConnection by remember {
        mutableStateOf(true)
    }

    val ctx = LocalContext.current
    val owner = LocalLifecycleOwner.current

    internetConnection = ctx.checkInternet()

    userDataViewModel.getUserId().collectAsState(initial = "").value.let {
        userId = it
    }

    if (fosName.isEmpty() && internetConnection || tryAgain) {

       LaunchedEffect(key1 = Unit, block = {
           userDataViewModel.getFosDetails(uid).observe(owner) {
               when (it) {
                   is NetworkResult.Error -> {
                       error = true
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
       })


    }

    if (users.isEmpty() && internetConnection || tryAgain) {
        LaunchedEffect(key1 = Unit, block = {
            userDataViewModel.userOfFos(uid).observe(owner) {
                when (it) {
                    is NetworkResult.Error -> {
                        timber("UsersOfFos ::: ERR")
                        error = true
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
        })
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
                AppBarFos(navHostController, topBarColor) {
                    appbarHeight = it
                }
            },
            sheetContent = {
                Content(navHostController,sheetHeight, animateRotation, users)
            },
            scaffoldState = scaffoldState,
            sheetElevation = radius.dp / 2,
            sheetShape = RoundedCornerShape(radius.dp, radius.dp, 0.dp, 0.dp),
            sheetBackgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.onBackground,
        ) {
            Box(
                Modifier
                    .background(Color.LightGray)
                    .onGloballyPositioned { layoutCoordinates ->
                        val b = layoutCoordinates.size.height
                        contentHeight = b.toFloat()
                    })
            {
                BaseFosData(totalSteps, userCount, fosName, userBio)
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
private fun BaseFosData(
    totalSteps: Int,
    userCount: Int,
    fosName: String,
    userBio: String
){
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
                style = fontBlack24(MaterialTheme.colors.onPrimary)
            )
        }


        Box(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.primaryVariant)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                text = userBio,
                style = fontRegular12(MaterialTheme.colors.primary)
            )
        }

    }
}

@Composable
private fun AppBarFos(
    navHostController: NavHostController,
    topBarColor: RGBColor,
    appbarHeight: (Float) -> Unit,
) {
    TopAppBar(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                val height = layoutCoordinates.size.height
                appbarHeight.invoke(height.toFloat())
            },
        backgroundColor = Color(topBarColor.red, topBarColor.green, topBarColor.blue),
        title = {
            Text(
                text = stringResource(id = R.string.field),
                style = fontMedium14(
                    MaterialTheme.colors.secondary
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
                    contentDescription = null,
                    tint = MaterialTheme.colors.secondary
                )
            }
        }
    )
}

@Composable
private fun Content(
    navHostController: NavHostController,
    sheetHeight: Dp,
    animateRotation: Animatable<Float, AnimationVector1D>,
    users: List<UserRes>,
) {
    LazyColumn(
        modifier = Modifier
            .height(sheetHeight)
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                text = stringResource(id = R.string.members),
                style = fontBold14(
                    MaterialTheme.colors.secondary
                )
            )
        }

        items(users) {
            Record(
                modifier = Modifier.padding(bottom = 8.dp),
                uid = it.uid,
                record = it.rank.orZero() - 1,
                title = it.fullName.orEmpty(),
                steps = it.steps.orZero(),
                onClick = {
                    navHostController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
                }
            )
        }


    }
}