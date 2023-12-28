package com.ntg.stepcounter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.EditText
import com.ntg.stepcounter.ui.theme.PRIMARY100
import com.ntg.stepcounter.ui.theme.PRIMARY500
import com.ntg.stepcounter.ui.theme.PRIMARY900
import com.ntg.stepcounter.ui.theme.TERTIARY100
import com.ntg.stepcounter.ui.theme.TERTIARY500
import com.ntg.stepcounter.ui.theme.TERTIARY900
import com.ntg.stepcounter.ui.theme.fontMedium12
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.vm.UserDataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
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
private fun Content(navHostController: NavHostController, paddingValues: PaddingValues,userDataViewModel: UserDataViewModel){

    val isVerified = userDataViewModel.isVerified().collectAsState(initial = null)

    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 32.dp)){

        item {
            if (isVerified.value != null){
                Box(modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isVerified.value.orFalse()) PRIMARY100 else TERTIARY100)) {
                    Text(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp), text = stringResource(id = R.string.account_pending), style = fontMedium12(
                        if (isVerified.value.orFalse()) PRIMARY900 else TERTIARY900))
                }
            }


            EditText(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), label = stringResource(id = R.string.first_last_name))
            EditText(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), label = stringResource(id = R.string.student_id))
            EditText(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), label = stringResource(id = R.string.field_study))
            EditText(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), label = stringResource(id = R.string.grade))
            
            CustomButton(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), text = stringResource(id = R.string.save), size = ButtonSize.XL)
        }

    }

}