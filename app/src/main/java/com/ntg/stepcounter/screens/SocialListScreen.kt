package com.ntg.stepcounter.screens

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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.ItemOption
import com.ntg.stepcounter.models.SocialNetwork
import com.ntg.stepcounter.models.components.AppbarItem
import com.ntg.stepcounter.vm.SocialNetworkViewModel

@Composable
fun SocialListScreen(
    navHostController: NavHostController,
    socialNetworkViewModel: SocialNetworkViewModel
){

    var searchWord by remember {
        mutableStateOf("")
    }
    val enableSearchBar = remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.socila_network),
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
            Content(navHostController = navHostController,paddingValues = innerPadding, socialNetworkViewModel = socialNetworkViewModel, searchWord)
        }
    )
}

@Composable
private fun Content(navHostController: NavHostController, paddingValues: PaddingValues,socialNetworkViewModel: SocialNetworkViewModel, search: String){

    LazyColumn{
        items(socialNetworkViewModel.socialNetworks.filter { it.name.contains(search) }){
            ItemOption(text = it.name) {socialSelected ->
                socialNetworkViewModel.socialNetworks.forEach {
                    it.isSelected = socialSelected == it.name
                }
                navHostController.popBackStack()
            }
        }
    }

}