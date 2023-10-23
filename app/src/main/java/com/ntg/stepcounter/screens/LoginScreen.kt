package com.ntg.stepcounter.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.EditText
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.util.extension.isValidIranMobileNumber
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.util.extension.toast
import com.ntg.stepcounter.vm.LoginViewModel

@Composable
fun LoginScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel
){
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.app_name_farsi),
                enableNavigation = false
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navHostController, loginViewModel)
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, navHostController: NavHostController, loginViewModel: LoginViewModel){

    val owner = LocalLifecycleOwner.current
    val ctx = LocalContext.current

    val phoneNumber = remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }


    Column(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 32.dp)) {
        EditText(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp), label = stringResource(id = R.string.phone_number), text = phoneNumber, maxLength = 11, keyboardType = KeyboardType.Number)

        CustomButton(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp), text = stringResource(id = R.string.next), size = ButtonSize.XL, loading = loading){

            if (phoneNumber.value.isEmpty()){
                ctx.toast(ctx.getString(R.string.phone_can_not_empty))
                return@CustomButton
            }else if (phoneNumber.value.isValidIranMobileNumber()){
                ctx.toast(ctx.getString(R.string.wrong_format_phone))
                return@CustomButton
            }


            if (phoneNumber.value.isNotEmpty()){
                loginViewModel.login(phoneNumber.value).observe(owner){

                    when(it){
                        is NetworkResult.Error -> {
                            loading = false
                            ctx.toast(ctx.getString(R.string.sth_wrong))
                            timber("LoginERR ::: ${it.message.orEmpty()}")
                        }
                        is NetworkResult.Loading -> {
                            loading = true
                        }
                        is NetworkResult.Success -> {
                            loading = false

                            if (it.data?.isSuccess.orFalse()){
                                if (it.data?.message.orEmpty() == "-1"){
                                    navHostController.navigate(Screens.RegisterScreen.name + "?phone=${phoneNumber.value}")
                                }else{
                                    navHostController.navigate(Screens.SignInScreen.name + "?phone=${phoneNumber.value}&state=${it.data?.message.orEmpty()}")
                                }
                            }else{
                                ctx.toast(ctx.getString(R.string.sth_wrong))
                            }
                        }
                    }

                }
            }

        }
    }

}

