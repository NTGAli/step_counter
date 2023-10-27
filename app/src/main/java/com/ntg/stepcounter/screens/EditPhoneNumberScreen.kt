package com.ntg.stepcounter.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycling
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.EditText
import com.ntg.stepcounter.components.Loading
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY900
import com.ntg.stepcounter.ui.theme.TERTIARY100
import com.ntg.stepcounter.ui.theme.TERTIARY900
import com.ntg.stepcounter.ui.theme.fontMedium12
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.toast
import com.ntg.stepcounter.vm.LoginViewModel
import com.ntg.stepcounter.vm.UserDataViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditPhoneNumberScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel,
    userDataViewModel: UserDataViewModel
){
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.edit_phone_number),
                navigationOnClick = {navHostController.popBackStack()}
            )
        },
        content = {
            Content(navHostController, loginViewModel, userDataViewModel)
        }
    )
}

@Composable
private fun Content(navHostController: NavHostController, loginViewModel: LoginViewModel, userDataViewModel: UserDataViewModel){

    val isVerified = userDataViewModel.isVerified().collectAsState(initial = null).value
    val uid = userDataViewModel.getUserId().collectAsState(initial = null).value
    val phone = remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    userDataViewModel.getPhoneNumber().collectAsState(initial = null).value.let {
        phone.value = it ?: ""
    }

    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current

    if (isVerified != null){

        Column(modifier = Modifier.padding(horizontal = 32.dp)) {

            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isVerified) PRIMARY100 else TERTIARY100)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                    text = if (isVerified) stringResource(id = R.string.account_verified) else stringResource(
                        id = R.string.account_pending
                    ),
                    style = fontMedium12(
                        if (isVerified) PRIMARY900 else TERTIARY900
                    )
                )
            }

            EditText(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp), label = stringResource(
                id = R.string.phone_number
            ), text = phone,
                enabled = !isVerified)
            
            CustomButton(modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(), text = stringResource(id = R.string.edit), size = ButtonSize.XL, loading = loading){

                if (isVerified){
                    context.toast(context.getString(R.string.cant_edit_phone))
                }else{

                    if (uid.orEmpty().isNotEmpty()){
                        loginViewModel.editPhoneNumber(phone.value, uid!!).observe(owner){
                            when(it){
                                is NetworkResult.Error -> {
                                    context.toast(context.getString(R.string.sth_wrong))
                                    loading = false
                                }
                                is NetworkResult.Loading -> {
                                    loading = true
                                }
                                is NetworkResult.Success -> {
                                    if (it.data?.isSuccess.orFalse()){
                                        userDataViewModel.setPhone(it.data?.data.orEmpty())
                                        context.toast(context.getString(R.string.phone_updated))
                                        navHostController.popBackStack()
                                    }else{
                                        context.toast(context.getString(R.string.sth_wrong))
                                    }
                                    loading = false
                                }
                            }
                        }
                    }

                }

            }
        }
        


    }else{
        Loading()
    }



}