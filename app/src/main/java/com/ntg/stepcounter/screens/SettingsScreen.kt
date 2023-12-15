package com.ntg.stepcounter.screens

import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import android.webkit.WebView
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.ntg.stepcounter.R
import android.webkit.WebViewClient
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import com.ntg.stepcounter.BuildConfig
import com.ntg.stepcounter.MainActivity.Companion.sensorType
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.ItemOption
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.fontBold14
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.sendMail
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.util.extension.toast
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun  Content(navHostController: NavHostController, paddingValues: PaddingValues, userDataViewModel: UserDataViewModel){

    val showToOther = remember {
        mutableStateOf(true)
    }

    val autoDetect = remember {
        mutableStateOf(true)
    }

    var loadingLock by remember {
        mutableStateOf(false)
    }

    val userStatus = remember {
        mutableStateOf("")
    }

    val theme = remember {
        mutableStateOf("")
    }

    val ctx = LocalContext.current
    val owner = LocalLifecycleOwner.current

    showToOther.value = userDataViewModel.isShowReport().collectAsState(initial = true).value
    autoDetect.value = userDataViewModel.isAutoDetect().collectAsState(initial = true).value
    userStatus.value = userDataViewModel.getUserStatus().collectAsState(initial = "").value
    theme.value = userDataViewModel.getTheme().collectAsState(initial = "light").value
    val uid = userDataViewModel.getUserId().collectAsState(initial = "").value

    val userPhone = userDataViewModel.getPhoneNumber().collectAsState(initial = "").value

    LazyColumn(modifier = Modifier.padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally){

        item {
            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.account), style = fontBold14(
                MaterialTheme.colors.primary))

            ItemOption(text = stringResource(id = R.string.change_account), onClick = {
                if (userStatus.value == "1"){
                    navHostController.navigate(Screens.RegisterScreen.name + "?phone=$userPhone&edit=${true}")
                }else if (userStatus.value == "2"){
                    navHostController.navigate(Screens.ProfRegisterScreen.name + "?phone=$userPhone&edit=${true}")
                }
            })

            ItemOption(text = stringResource(id = R.string.phone_number), onClick = {
                navHostController.navigate(Screens.EditPhoneNumberScreen.name)
            })


            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.theme), style = fontBold14(MaterialTheme.colors.primary))


            ItemOption(text = if (theme.value == "light") stringResource(id = R.string.light_mode) else stringResource(id = R.string.dark_mode), onClick = {
                if (theme.value == "light") userDataViewModel.setTheme("dark") else userDataViewModel.setTheme("light")
            })


            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.privacy), style = fontBold14(MaterialTheme.colors.primary))

            ItemOption(text = stringResource(id = R.string.show_report_to_others), switchChecked = showToOther, enableSwitch = !loadingLock, subText = stringResource(
                R.string.show_other_desc
            ), onClick = {

                userDataViewModel.setLock(uid =  uid, !showToOther.value).observe(owner){
                    when(it){
                        is NetworkResult.Error -> {
                            ctx.toast(ctx.getString(R.string.sth_wrong))
                            loadingLock = false
                        }
                        is NetworkResult.Loading -> {
                            loadingLock = true
                        }
                        is NetworkResult.Success -> {
                            if (it.data?.isSuccess.orFalse()){
                                userDataViewModel.isShowReport(it.data?.data.orFalse())
                            }else{
                                ctx.toast(ctx.getString(R.string.sth_wrong))
                            }
                            loadingLock = false
                        }
                    }
                }

            })

            ItemOption(text = stringResource(id = R.string.privacy_policies), onClick = {
                navHostController.navigate(Screens.PrivacyPolicyScreen.name)
            })


            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.advance), style = fontBold14(MaterialTheme.colors.primary))

            ItemOption(text = stringResource(id = R.string.step_auto_detect), switchChecked = autoDetect, enableSwitch = true, subText = stringResource(
                R.string.auto_detect_desc
            ), onClick = {
                userDataViewModel.isAutoDetect(!autoDetect.value)
            })


            Text(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 8.dp), text = stringResource(id = R.string.other), style = fontBold14(MaterialTheme.colors.primary))

            ItemOption(text = stringResource(id = R.string.terms_and_conditions), onClick = {
                navHostController.navigate(Screens.TermAndConditionsScreen.name)
            })

            ItemOption(text = stringResource(id = R.string.contact_us), onClick = {
                ctx.sendMail(ctx.getString(R.string.support_email),ctx.getString(R.string.contact_us))
            })

            ItemOption(text = stringResource(id = R.string.report_problem), onClick = {
                ctx.sendMail(ctx.getString(R.string.support_email),ctx.getString(R.string.report_problem))
            })

            Text(modifier = Modifier.padding(top = 24.dp).combinedClickable(
                onLongClick = {
                    ctx.toast(sensorType)
                },
                onClick = {}
            ), text = stringResource(id = R.string.version,BuildConfig.VERSION_NAME), style = fontRegular12(
                MaterialTheme.colors.primary))
        }

    }

}