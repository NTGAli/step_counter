package com.ntg.stepi.screens

import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.ntg.stepi.R
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.components.ItemOption
import com.ntg.stepi.models.components.AppbarItem
import com.ntg.stepi.vm.UserDataViewModel

@Composable
fun SelectLanguageScreen(
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
                title = stringResource(R.string.language),
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
            Content(
                navHostController = navHostController,
                paddingValues = innerPadding,
                userDataViewModel = userDataViewModel,
                search = searchWord
            )
        }
    )
}

@Composable
private fun Content(navHostController: NavHostController, paddingValues: PaddingValues, userDataViewModel: UserDataViewModel, search: String){

    val ctx = LocalContext.current


    val languages = remember {
        mutableStateOf(listOf<String>())
    }

    languages.value = listOf(
        stringResource(id = R.string.persian),
        stringResource(id = R.string.arabic),
//        stringResource(id = R.string.english),
    )


    LazyColumn{
        items(languages.value.filter { it.contains(search) }){
            ItemOption(text = it) {language ->
                userDataViewModel.setLanguage(if (language == ctx.getString(R.string.persian)) "fa" else if (language == ctx.getString(R.string.arabic)) "ar" else "en")
                navHostController.popBackStack()
            }
        }
    }

}