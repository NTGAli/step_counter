package com.ntg.stepcounter.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.Loading
import com.ntg.stepcounter.components.Record
import com.ntg.stepcounter.models.res.SummaryRes
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.util.extension.toast
import com.ntg.stepcounter.vm.StepViewModel
import com.ntg.stepcounter.vm.UserDataViewModel



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SeeMoreScreen(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel,
    stepViewModel: StepViewModel,
    type: String?
){
    var appBarTitle by remember {
        mutableStateOf("")
    }

    when(type){

        "TopToday" ->{
            appBarTitle = stringResource(id = R.string.top_today)
        }

        "TopBaseFos" -> {
            appBarTitle = stringResource(id = R.string.top_rank_base_fos)
        }

        "TopUsers" -> {
            appBarTitle = stringResource(id = R.string.top_rank_base_user)
        }

        null -> {
            navHostController.popBackStack()
        }

    }

    Scaffold(
        topBar = {
            Appbar(
                title = appBarTitle,
                navigationOnClick = { navHostController.popBackStack() }
            )
        },
        content = {
            Content(navHostController = navHostController, userDataViewModel, stepViewModel, type.orEmpty())
        }
    )
}

@Composable
private fun Content(navHostController: NavHostController, userDataViewModel: UserDataViewModel, stepViewModel: StepViewModel, base: String){

    var users by remember {
        mutableStateOf(listOf<SummaryRes>())
    }

    var loading by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current


    if (users.isEmpty()){

        stepViewModel.gerUserBase(base).observe(LocalLifecycleOwner.current){
            when(it){
                is NetworkResult.Error -> {
                    loading = false
                    context.toast(context.getString(R.string.sth_wrong))
                }
                is NetworkResult.Loading -> {
                    loading = true
                }
                is NetworkResult.Success -> {
                    users = it.data?.data.orEmpty()
                    loading = false
                }
            }
        }

    }



    if (loading)
        Loading()

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp), content = {

        itemsIndexed(users){index, it ->
            Record(
                modifier = Modifier.padding(top = 8.dp),
                uid = it.uid,
                record = index,
                title = it.title.orEmpty(),
                steps = it.steps,
                onClick = {
                    if (base == "TopBaseFos"){
                        navHostController.navigate(Screens.FieldOfStudyDetailsScreen.name + "?uid=$it&rank=${index+1}")
                    }else{
                        navHostController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
                    }

                }
            )
        }

    })



}