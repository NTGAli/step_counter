package com.ntg.stepi.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.stepi.R
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.components.CustomButton
import com.ntg.stepi.components.EditText
import com.ntg.stepi.components.Loading
import com.ntg.stepi.util.extension.toast
import com.ntg.stepi.vm.LoginViewModel

@Composable
fun AddFosScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel
){
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.field_study),
                navigationOnClick = { navHostController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navHostController, loginViewModel)
        }
    )


}

@Composable
private fun Content(paddingValues: PaddingValues, navHostController: NavHostController, loginViewModel: LoginViewModel){
    val ctx = LocalContext.current
    val owner = LocalLifecycleOwner.current

    val fosName = remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    if (loading) Loading()
    else{
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 32.dp)
            .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            EditText(modifier = Modifier.fillMaxWidth(),text = fosName, label = stringResource(id = R.string.field_study))

            CustomButton(modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(), text = stringResource(id = R.string.insert), size = ButtonSize.LG){


                loginViewModel.insertNewFos(fosName.value).observe(owner){

                    when(it){
                        is NetworkResult.Error -> {

                        }
                        is NetworkResult.Loading -> {
                            loading = true
                        }
                        is NetworkResult.Success -> {
                            loading = false
                            if (it.data?.data != null){
                                ctx.toast(ctx.getString(R.string.fos_inserted))
                                navHostController.popBackStack()
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