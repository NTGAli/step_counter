package com.ntg.stepcounter.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.EditText
import com.ntg.stepcounter.models.Social
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.util.extension.toast
import com.ntg.stepcounter.vm.SocialNetworkViewModel

@Composable
fun SocialScreen(
    navHostController: NavHostController,
    socialNetworkViewModel: SocialNetworkViewModel,
    id: Int?
){

    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.socila_network),
                navigationOnClick = { navHostController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navHostController, socialNetworkViewModel, id)
        }
    )


}

@Composable
private fun Content(paddingValues: PaddingValues, navHostController: NavHostController, socialNetworkViewModel: SocialNetworkViewModel, id: Int?){
    val ctx = LocalContext.current
    Column(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 32.dp)
        .padding(top = 16.dp)) {

        val socialName = remember {
            mutableStateOf("")
        }

        var _pageId by rememberSaveable {
            mutableStateOf("")
        }

        val pageId = remember {
            mutableStateOf(_pageId)
        }

        socialName.value = try {
            socialNetworkViewModel.socialNetworks.filter { it.isSelected }[0].name
        }catch (e: Exception){
            ""
        }

        if (id != -1 && id != null && pageId.value.isEmpty()){
            val sName = socialNetworkViewModel.getSocial(id).observeAsState().value?.name ?: ""
            socialNetworkViewModel.socialNetworks.forEach {
                it.isSelected = sName == it.name
            }
            pageId.value = socialNetworkViewModel.getSocial(id).observeAsState().value?.pageId ?: ""
        }


        EditText(modifier = Modifier.fillMaxWidth(),text = socialName, label = stringResource(id = R.string.scoial_name), readOnly = true, onClick = {
            navHostController.navigate(Screens.SocialListScreen.name)
        })
        EditText(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),text = pageId, label = stringResource(id = R.string.id_or_page_name), onChange = {
            _pageId = it
        })
        
        CustomButton(modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth(), text = if (id == null || id == -1) stringResource(id = R.string.save) else stringResource(id = R.string.edit), size = ButtonSize.LG){
            if (socialName.value.isEmpty()){
                ctx.toast(ctx.getString(R.string.select_social))
                return@CustomButton
            }else if (pageId.value.isEmpty()){
                ctx.toast(ctx.getString(R.string.page_id_empty))
                return@CustomButton
            }
            if (id == null || id == -1){
                socialNetworkViewModel.insertNew(Social(0, socialName.value, pageId.value))
            }else{
                socialNetworkViewModel.update(Social(id, socialName.value, pageId.value))
            }
            navHostController.popBackStack()
        }
    }
}