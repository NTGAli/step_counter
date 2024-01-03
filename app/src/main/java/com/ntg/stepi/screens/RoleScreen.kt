package com.ntg.stepi.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ntg.stepi.R
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.components.RoleItem
import com.ntg.stepi.nav.Screens


@Composable
fun RoleScreen(
    navHostController: NavHostController,
    phone: String
){

    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.im),
                navigationOnClick = { navHostController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navHostController, phone)
        }
    )


}

@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController, phone: String){

    val roles = listOf(
       Pair(1, stringResource(id = R.string.im_student)),
       Pair(2, stringResource(id = R.string.im_prof)),
       Pair(3, stringResource(id = R.string.im_employee)),
    )

    LazyColumn(modifier = Modifier.padding(paddingValues), content = {

        items(roles){

            RoleItem(modifier = Modifier.padding(top = 8.dp).padding(horizontal = 16.dp), id = it.first, title = it.second, onClick = {
                when(it){

                    1 -> {
                        navController.navigate(Screens.RegisterScreen.name + "?phone=$phone")
                    }

                    2 -> {
                        navController.navigate(Screens.ProfRegisterScreen.name + "?phone=$phone")
                    }

                    3 -> {
                        navController.navigate(Screens.EmployeeScreen.name + "?phone=$phone")
                    }

                }
            })

        }

    })

}