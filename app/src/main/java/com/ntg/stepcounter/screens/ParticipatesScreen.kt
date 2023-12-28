package com.ntg.stepcounter.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.DataLeague
import com.ntg.stepcounter.components.Loading
import com.ntg.stepcounter.components.Record
import com.ntg.stepcounter.models.res.SummaryRes
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.vm.StepViewModel

@Composable
fun ParticipatesScreen(
    navController: NavController,
    stepViewModel: StepViewModel,
    uid: String
){

    val owner = LocalLifecycleOwner.current

    var rank by remember {
        mutableStateOf("-")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var leagueName by remember {
        mutableStateOf("-")
    }

    var timeLeft by remember {
        mutableStateOf("-")
    }

    var numberOfUsers by remember {
        mutableIntStateOf(0)
    }

    var users by remember {
        mutableStateOf(listOf<SummaryRes>())
    }

    LaunchedEffect(key1 = loading, block = {

        stepViewModel.dataChallenge(uid).observe(owner){

            when(it){
                is NetworkResult.Error -> {
                    loading = false
                }
                is NetworkResult.Loading -> {
                    loading = true
                }
                is NetworkResult.Success -> {

                    rank = it.data?.data?.rank ?: "-"
                    leagueName = it.data?.data?.leagueName ?: "-"
                    timeLeft = it.data?.data?.timeLeft ?: "-"
                    users = it.data?.data?.users ?: listOf()
                    numberOfUsers = it.data?.data?.numberOfUsers.orZero()

                }
            }

        }

    })


    if (leagueName == "-"){
        Loading()
    }else{
        LazyColumn(content = {

            item {
                DataLeague(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    title = leagueName,
                    subTitle = stringResource(id = R.string.participents_format, numberOfUsers),
                    first = stringResource(id = R.string.days_format, timeLeft),
                    second = rank
                )

            }

            itemsIndexed(users){index,user ->
                Record(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp),
                    uid = user.uid,
                    record = index,
                    title = user.title.orEmpty(),
                    steps = user.steps,
                    primaryBorder = user.uid == uid,
                    onClick = {
                        navController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
                    }
                )
            }

        })
    }





}