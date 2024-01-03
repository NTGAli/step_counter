package com.ntg.stepi.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.components.WinnerItem
import com.ntg.stepi.models.res.UserWinnerData
import com.ntg.stepi.nav.Screens
import com.ntg.stepi.util.extension.openInBrowser
import com.ntg.stepi.util.extension.orZero
import com.ntg.stepi.vm.StepViewModel

@Composable
fun WinnersScreen(
    navController: NavController,
    stepViewModel: StepViewModel
){

    var loading by remember {
        mutableStateOf(false)
    }

    var loaded by remember {
        mutableStateOf(false)
    }

    var winners by rememberSaveable {
        mutableStateOf(listOf<UserWinnerData>())
    }

    val owner = LocalLifecycleOwner.current
    val ctx = LocalContext.current

    LaunchedEffect(key1 = loading, block = {

        stepViewModel.winners().observe(owner){

            when(it){
                is NetworkResult.Error -> {
                    loading = false
                }
                is NetworkResult.Loading -> {
                    loading = true
                }
                is NetworkResult.Success -> {

                    loaded = true
                    winners = it.data?.data ?: listOf()

                }
            }

        }

    })

    LazyColumn(modifier = Modifier.padding(top = 8.dp), content = {

        items(winners){

            WinnerItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
                uid = it.uid.orEmpty(),
                username = it.fullName.orEmpty(),
                leagueName = it.leagueName.orEmpty(),
                steps = it.steps.orZero(),
                days = it.days.orZero(),
                sponsor = it.sponsor,
                sponsorLink = it.sponsorLink,
                userClick = {
                    navController.navigate(Screens.UserProfileScreen.name + "?uid=$it")
                },
                sponsorClick = {
                    ctx.openInBrowser(it)
                }
            )

        }

    })


}