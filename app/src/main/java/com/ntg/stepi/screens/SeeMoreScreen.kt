package com.ntg.stepi.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ntg.stepi.R
import com.ntg.stepi.components.AdsItem
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.components.Loading
import com.ntg.stepi.components.Record
import com.ntg.stepi.models.res.SummaryRes
import com.ntg.stepi.nav.Screens
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
    stepViewModel.query.value = type.orEmpty()
    val uid = userDataViewModel.getUserId().collectAsState(initial = null).value

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

    val users = stepViewModel.userData.collectAsLazyPagingItems()

    Scaffold(topBar = {
        Appbar(title = appBarTitle, navigationOnClick = { navHostController.popBackStack() })
    }, content = {
        if (uid != null){
            Content(
                users,
                navHostController = navHostController,
                base = type.orEmpty(),
                uid = uid
            )
        }
    })
}

@Composable
private fun Content(
    users: LazyPagingItems<SummaryRes>,
    navHostController: NavHostController,
    base: String,
    uid: String
) {
    when (users.loadState.refresh) {
        LoadState.Loading -> {
            Loading()
        }

        is LoadState.Error -> {
            LocalContext.current.toast(stringResource(id = R.string.sth_wrong))
        }

        else -> {
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(users.itemCount) { index ->
                    Record(
                        modifier = Modifier.padding(top = 8.dp),
                        uid = users[index]?.uid,
                        record = index,
                        title = users[index]?.title.orEmpty(),
                        steps = users[index]?.steps,
                        primaryBorder = users[index]?.uid == uid,
                        onClick = {
                            if (base == "TopBaseFos") {
                                navHostController.navigate(Screens.FieldOfStudyDetailsScreen.name + "?uid=$it&rank=${index + 1}")
                            } else {
                                navHostController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
                            }
                        }
                    )

                    if (users[index]?.ads != null) {
                        AdsItem(modifier = Modifier.padding(top = 8.dp), users[index]?.ads)
                    }
                }

                item {
                    Spacer(modifier = Modifier.padding(24.dp))
                }


            }
        }
    }
}