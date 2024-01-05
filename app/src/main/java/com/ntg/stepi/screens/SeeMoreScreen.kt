package com.ntg.stepi.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.ntg.stepi.R
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.components.AdsItem
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.components.Loading
import com.ntg.stepi.components.Record
import com.ntg.stepi.models.res.ADSRes
import com.ntg.stepi.models.res.SummaryRes
import com.ntg.stepi.nav.Screens
import com.ntg.stepi.util.extension.timber
import com.ntg.stepi.util.extension.toast
import com.ntg.stepi.vm.StepViewModel
import com.ntg.stepi.vm.UserDataViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SeeMoreScreen(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel,
    stepViewModel: StepViewModel,
    type: String?
) {
    var appBarTitle by remember {
        mutableStateOf("")
    }

    when (type) {

        "TopToday" -> {
            appBarTitle = stringResource(id = R.string.top_today)
        }

        "TopBaseFos" -> {
            appBarTitle = stringResource(id = R.string.top_rank_base_title)
        }

        "TopUsers" -> {
            appBarTitle = stringResource(id = R.string.top_rank_base_user)
        }

        null -> {
            navHostController.popBackStack()
        }

    }

    Scaffold(topBar = {
        Appbar(title = appBarTitle, navigationOnClick = { navHostController.popBackStack() })
    }, content = {
        Content(
            navHostController = navHostController,
            userDataViewModel,
            stepViewModel,
            type.orEmpty()
        )
    })
}

@Composable
private fun Content(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel,
    stepViewModel: StepViewModel,
    base: String
) {
    var loading by remember {
        mutableStateOf(false)
    }


    val uid = userDataViewModel.getUserId().collectAsState(initial = "").value

//    if (users.isEmpty()) {
//
//        stepViewModel.gerUserBase(base).observe(LocalLifecycleOwner.current) {
//            when (it) {
//                is NetworkResult.Error -> {
//                    loading = false
//                    context.toast(context.getString(R.string.sth_wrong))
//                }
//
//                is NetworkResult.Loading -> {
//                    loading = true
//                }
//
//                is NetworkResult.Success -> {
//                    users = it.data?.data?.users.orEmpty()
//                    ads = it.data?.data?.ads.orEmpty()
//                    adsNum = ads.size -1
//                    loading = false
//                }
//            }
//        }
//
//    }


    LaunchedEffect(key1 = Unit, block = {

    })
    val usersData = stepViewModel.getBreakingNews(base).collectAsLazyPagingItems()



    if (loading) Loading()

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp), content = {




        items(usersData.itemCount) { index ->
            Column {
                Record(modifier = Modifier.padding(top = 8.dp),
                    uid = usersData[index]?.uid,
                    record = index,
                    title = usersData[index]?.title.orEmpty(),
                    steps = usersData[index]?.steps,
                    primaryBorder = uid == usersData[index]?.uid,
                    onClick = {
                        if (base == "TopBaseFos") {
                            navHostController.navigate(Screens.FieldOfStudyDetailsScreen.name + "?uid=$it&rank=${index + 1}")
                        } else {
                            navHostController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
                        }

                    })

                if (usersData[index]?.ads != null) {
                    AdsItem(modifier = Modifier.padding(top = 8.dp), usersData[index]?.ads)
                }
            }

        }

        item {
            Spacer(modifier = Modifier.padding(24.dp))
        }


//        itemsIndexed(users) { index, it ->
//            Column {
//                Record(modifier = Modifier.padding(top = 8.dp),
//                    uid = it.uid,
//                    record = index,
//                    title = it.title.orEmpty(),
//                    steps = it.steps,
//                    primaryBorder = uid == it.uid,
//                    onClick = {
//                        if (base == "TopBaseFos") {
//                            navHostController.navigate(Screens.FieldOfStudyDetailsScreen.name + "?uid=$it&rank=${index + 1}")
//                        } else {
//                            navHostController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
//                        }
//
//                    })
//
//                if (ads.isNotEmpty() && (index+1) % 8 == 0) {
//
//                    AdsItem(modifier = Modifier.padding(top = 8.dp), ads[adsNum])
//                    if (adsNum < ads.size-1) adsNum++
//                    else adsNum = 0
//                }
//            }
//
//
//        }

    })


}