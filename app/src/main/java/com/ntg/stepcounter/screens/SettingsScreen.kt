package com.ntg.stepcounter.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.ItemOption
import com.ntg.stepcounter.models.components.AppbarItem
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontBold14

@Composable
fun SettingsScreen(
    navHostController: NavHostController
){
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.settings),
                navigationOnClick = { navHostController.popBackStack() },
                actions = listOf(
                    AppbarItem(
                        id = 0,
                        imageVector = Icons.Rounded.Search
                    )
                )
            )
        },
        content = { innerPadding ->
            Content(navHostController = navHostController,paddingValues = innerPadding)
        }
    )
}

@Composable
private fun  Content(navHostController: NavHostController, paddingValues: PaddingValues){

    LazyColumn(modifier = Modifier.padding(paddingValues)){

        item {
            Text(modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.account), style = fontBold14(SECONDARY500))

            ItemOption(text = stringResource(id = R.string.change_account), onClick = {

            })

            ItemOption(text = stringResource(id = R.string.phone_number), onClick = {

            })

            Text(modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.privacy), style = fontBold14(SECONDARY500))

            ItemOption(text = stringResource(id = R.string.show_report_to_others), onClick = {

            })

            ItemOption(text = stringResource(id = R.string.privacy_policies), onClick = {

            })


            Text(modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.advance), style = fontBold14(SECONDARY500))

            ItemOption(text = stringResource(id = R.string.step_auto_detect), onClick = {

            })


            Text(modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.other), style = fontBold14(SECONDARY500))

            ItemOption(text = stringResource(id = R.string.terms_and_conditions), onClick = {

            })

            ItemOption(text = stringResource(id = R.string.contact_us), onClick = {

            })

            ItemOption(text = stringResource(id = R.string.report_problem), onClick = {

            })
        }

    }

}