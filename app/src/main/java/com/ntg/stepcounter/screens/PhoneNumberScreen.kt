package com.ntg.stepcounter.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.EditText
import com.ntg.stepcounter.vm.UserDataViewModel

@Composable
fun PhoneNumberScreen(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel
){
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.account),
                navigationOnClick = { navHostController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(navHostController = navHostController,paddingValues = innerPadding, userDataViewModel)
        }
    )
}

@Composable
private fun Content(navHostController: NavHostController, paddingValues: PaddingValues, userDataViewModel: UserDataViewModel){


    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 32.dp)){

        item {
            EditText(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), label = stringResource(id = R.string.phone_number)
            )


            CustomButton(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), text = stringResource(id = R.string.save), size = ButtonSize.XL)
        }

    }

}