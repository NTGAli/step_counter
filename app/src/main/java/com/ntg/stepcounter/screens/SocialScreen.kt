package com.ntg.stepcounter.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.EditText
import com.ntg.stepcounter.models.Social
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.toast
import com.ntg.stepcounter.vm.SocialNetworkViewModel
import com.ntg.stepcounter.vm.UserDataViewModel

@Composable
fun SocialScreen(
    navHostController: NavHostController,
    socialNetworkViewModel: SocialNetworkViewModel,
    userDataViewModel: UserDataViewModel,
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
            Content(paddingValues = innerPadding, navHostController, socialNetworkViewModel,userDataViewModel, id)
        }
    )


}

@Composable
private fun Content(paddingValues: PaddingValues, navHostController: NavHostController, socialNetworkViewModel: SocialNetworkViewModel,userDataViewModel: UserDataViewModel, id: Int?){
    val ctx = LocalContext.current
    val owner = LocalLifecycleOwner.current

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

        val pageId = rememberSaveable {
            mutableStateOf(_pageId)
        }

        val uid = userDataViewModel.getUserId().collectAsState(initial = null).value

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
            pageId.value = socialNetworkViewModel.getSocial(id).observeAsState().value?.pageId?.split("/")?.last() ?: ""
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
            }else if (uid == null) return@CustomButton
            if (id == null || id == -1){

                socialNetworkViewModel.insertInServer(uid, Social(0, socialName.value,
                    socialNetworkViewModel.socialNetworks.first { it.isSelected.orFalse() }.link.replace("PAGE_NAME", pageId.value))).observe(owner){
                    when(it){
                        is NetworkResult.Error -> {
                            ctx.toast(ctx.getString(R.string.sth_wrong))
                        }
                        is NetworkResult.Loading -> {
                        }
                        is NetworkResult.Success -> {
                            if (it.data?.isSuccess.orFalse()){
                                socialNetworkViewModel.insertNew(Social(it.data?.data.orZero(), socialName.value, socialNetworkViewModel.socialNetworks.first { it.isSelected.orFalse() }.link.replace("PAGE_NAME", pageId.value)))
                            }else{
                                ctx.toast(ctx.getString(R.string.sth_wrong))
                            }
                            navHostController.popBackStack()
                        }
                    }
                }

            }else{

                socialNetworkViewModel.updateInServer(uid, Social(id, socialName.value,
                    socialNetworkViewModel.socialNetworks.first { it.isSelected.orFalse() }.link.replace("PAGE_NAME", pageId.value))).observe(owner){
                    when(it){
                        is NetworkResult.Error -> {
                            ctx.toast(ctx.getString(R.string.sth_wrong))
                        }
                        is NetworkResult.Loading -> {
                        }
                        is NetworkResult.Success -> {
                            socialNetworkViewModel.update(Social(it.data?.data.orZero(), socialName.value, socialNetworkViewModel.socialNetworks.first { it.isSelected.orFalse() }.link.replace("PAGE_NAME", pageId.value)))
                            navHostController.popBackStack()
                        }
                    }
                }

            }
        }
    }

}

private fun insertNewSocial(owner: LifecycleOwner, context: Context, uid: String, name: String, page: String,socialNetworkViewModel: SocialNetworkViewModel, onResult:(Boolean) -> Unit){





}