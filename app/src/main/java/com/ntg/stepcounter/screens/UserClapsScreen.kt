package com.ntg.stepcounter.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.ItemOption
import com.ntg.stepcounter.components.Loading
import com.ntg.stepcounter.models.FieldOfStudy
import com.ntg.stepcounter.models.components.AppbarItem
import com.ntg.stepcounter.models.res.UserRes
import com.ntg.stepcounter.vm.LoginViewModel
import com.ntg.stepcounter.vm.UserDataViewModel

@Composable
fun UserClapsScreen(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel
){

    var searchWord by remember {
        mutableStateOf("")
    }
    val enableSearchBar = remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.users),
                navigationOnClick = { navHostController.popBackStack() },
                actions = listOf(
                    AppbarItem(
                        id = 0,
                        imageVector = Icons.Rounded.Search
                    )
                ),
                enableSearchbar = enableSearchBar,
                actionOnClick = {
                    enableSearchBar.value = true
                },
                onQueryChange = { query ->
                    searchWord = query
                },
            )
        },
        content = { innerPadding ->
            Content(navHostController = navHostController,paddingValues = innerPadding, userDataViewModel = userDataViewModel, searchWord)
        }
    )
}

@Composable
private fun Content(navHostController: NavHostController, paddingValues: PaddingValues, userDataViewModel: UserDataViewModel, search: String){

    val owner = LocalLifecycleOwner.current
    var loading by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf(false)
    }

    val userClaps = remember {
        mutableStateOf(listOf<UserRes>())
    }

    val uid = userDataViewModel.getUserId().collectAsState(initial = null).value

    if (userClaps.value.isEmpty() && uid != null){
        userDataViewModel.clapsData(uid).observe(owner){
            when (it){
                is NetworkResult.Error -> {
                    error = true
                    loading = false
                }
                is NetworkResult.Loading -> {
                    loading = true
                }
                is NetworkResult.Success -> {
                    userClaps.value = it.data?.data.orEmpty()
                    loading = false
                }
            }
        }
    }


    if (loading) Loading()

    LazyColumn{
        items(userClaps.value.filter { it.fullName.orEmpty().contains(search) }){
            ItemOption(text = it.fullName.orEmpty()) {fosSelected ->
//                loginViewModel.fieldOfStudy = listOfStudies.value.first { it.title == fosSelected }
//                navHostController.popBackStack()
            }
        }
    }

}