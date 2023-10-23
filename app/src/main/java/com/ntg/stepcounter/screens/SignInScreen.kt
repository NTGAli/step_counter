package com.ntg.stepcounter.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.EditText
import com.ntg.stepcounter.vm.LoginViewModel
import com.ntg.stepcounter.vm.UserDataViewModel
import kotlinx.coroutines.launch


@Composable
fun SignInScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel,
    userDataViewModel: UserDataViewModel,
    phoneNumber: String?,
    state: String?
){
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.sign_in),
                navigationOnClick = { navHostController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navHostController = navHostController, loginViewModel, userDataViewModel, state)
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, navHostController: NavHostController, loginViewModel: LoginViewModel, userDataViewModel: UserDataViewModel, state: String?){

    val uid = remember {
        mutableStateOf("")
    }

    Column(modifier = Modifier.padding(horizontal = 32.dp)) {

        EditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            label = stringResource(id = if (state.orEmpty() == "1") R.string.student_id else R.string.prof_id),
            text = uid
        )

        CustomButton(modifier = Modifier.padding(top = 16.dp).fillMaxWidth(), text = stringResource(id = R.string.sign_in), size = ButtonSize.XL){

        }
    }



}