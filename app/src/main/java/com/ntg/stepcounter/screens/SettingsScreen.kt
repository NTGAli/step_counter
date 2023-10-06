package com.ntg.stepcounter.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.ItemOption
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontBold14
import com.ntg.stepcounter.util.extension.sendMail
import com.ntg.stepcounter.vm.UserDataViewModel

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel
){
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.settings),
                navigationOnClick = { navHostController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(navHostController = navHostController,paddingValues = innerPadding, userDataViewModel)
        }
    )
}

@Composable
private fun  Content(navHostController: NavHostController, paddingValues: PaddingValues, userDataViewModel: UserDataViewModel){

    val showToOther = remember {
        mutableStateOf(true)
    }

    val autoDetect = remember {
        mutableStateOf(true)
    }

    val ctx = LocalContext.current

    showToOther.value = userDataViewModel.isShowReport().collectAsState(initial = true).value
    autoDetect.value = userDataViewModel.isAutoDetect().collectAsState(initial = true).value

    LazyColumn(modifier = Modifier.padding(paddingValues)){

        item {
            Text(modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.account), style = fontBold14(SECONDARY500))

            ItemOption(text = stringResource(id = R.string.change_account), onClick = {
                navHostController.navigate(Screens.AccountScreen.name)
            })

            ItemOption(text = stringResource(id = R.string.phone_number), onClick = {

            })

            Text(modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.privacy), style = fontBold14(SECONDARY500))

            ItemOption(text = stringResource(id = R.string.show_report_to_others), switchChecked = showToOther, enableSwitch = true, subText = stringResource(
                R.string.show_other_desc
            ), onClick = {
                userDataViewModel.isShowReport(!showToOther.value)
//                showToOther.value = !showToOther.value
            })

            ItemOption(text = stringResource(id = R.string.privacy_policies), onClick = {

            })


            Text(modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.advance), style = fontBold14(SECONDARY500))

            ItemOption(text = stringResource(id = R.string.step_auto_detect), switchChecked = autoDetect, enableSwitch = true, subText = stringResource(
                R.string.auto_detect_desc
            ), onClick = {
                userDataViewModel.isAutoDetect(!autoDetect.value)
            })


            Text(modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.other), style = fontBold14(SECONDARY500))

            ItemOption(text = stringResource(id = R.string.terms_and_conditions), onClick = {

            })

            ItemOption(text = stringResource(id = R.string.contact_us), onClick = {
                ctx.sendMail(ctx.getString(R.string.support_email),ctx.getString(R.string.contact_us))
            })

            ItemOption(text = stringResource(id = R.string.report_problem), onClick = {
                ctx.sendMail(ctx.getString(R.string.support_email),ctx.getString(R.string.report_problem))
            })
        }

    }

}