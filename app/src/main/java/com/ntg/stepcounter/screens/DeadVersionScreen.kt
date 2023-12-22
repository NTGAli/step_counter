package com.ntg.stepcounter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.fontMedium14

@Composable
fun DeadVersionScreen(
    navHostController: NavHostController
) {

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Icon(
            modifier = Modifier
                .padding(top = 64.dp)
                .background(
                    color = MaterialTheme.colors.primaryVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            painter = painterResource(id = R.drawable.arrow_down_square_contained),
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )


        Text(modifier = Modifier.padding(top = 24.dp), text = stringResource(id = R.string.dead_version_description), style = fontMedium14(MaterialTheme.colors.secondary))
        
        CustomButton(modifier = Modifier.fillMaxWidth().padding(top = 8.dp).padding(horizontal = 32.dp), text = stringResource(id = R.string.download), style = ButtonStyle.TextOnly, size = ButtonSize.MD){
            navHostController.navigate(Screens.UpdateScreen.name)
        }

    }

}