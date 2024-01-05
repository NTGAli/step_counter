package com.ntg.stepi.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepi.R
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.components.CustomButton
import com.ntg.stepi.components.ItemOption
import com.ntg.stepi.components.Loading
import com.ntg.stepi.models.FieldOfStudy
import com.ntg.stepi.models.components.AppbarItem
import com.ntg.stepi.nav.Screens
import com.ntg.stepi.vm.LoginViewModel

@Composable
fun JobScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel
){

    var searchWord by remember {
        mutableStateOf("")
    }
    val enableSearchBar = remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.job),
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
            Content(navHostController = navHostController,paddingValues = innerPadding, loginViewModel = loginViewModel, searchWord)
        }
    )
}

@Composable
private fun Content(navHostController: NavHostController, paddingValues: PaddingValues, loginViewModel: LoginViewModel, search: String){

    val owner = LocalLifecycleOwner.current
    var loading by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf(false)
    }

    val listOfStudies = remember {
        mutableStateOf(listOf<FieldOfStudy>())
    }

    if (listOfStudies.value.isEmpty()){
        loginViewModel.jobs().observe(owner){
            when (it){
                is NetworkResult.Error -> {
                    error = true
                    loading = false
                }
                is NetworkResult.Loading -> {
                    loading = true
                }
                is NetworkResult.Success -> {
                    listOfStudies.value = it.data?.data.orEmpty()
                    loading = false
                }
            }
        }
    }


    if (loading) Loading()

    val searchedList = listOfStudies.value.filter { it.title.orEmpty().contains(search) }

    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally){

        if (searchedList.isNotEmpty()){
            items(searchedList){
                ItemOption(text = it.title.orEmpty()) {fosSelected ->
                    loginViewModel.job = listOfStudies.value.first { it.title == fosSelected }
                    navHostController.popBackStack()
                }
            }
        }
//        else if (!loading){
//
//            item {
//
//                CustomButton(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp ), text = stringResource(id = R.string.fos_not_in_list), size = ButtonSize.MD, style = ButtonStyle.TextOnly){
//                    navHostController.navigate(Screens.AddFosScreen.name)
//                }
//
//            }
//
//        }

    }

}