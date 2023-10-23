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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.components.EditText
import com.ntg.stepcounter.models.Social
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.toast
import com.ntg.stepcounter.vm.LoginViewModel
import com.ntg.stepcounter.vm.SocialNetworkViewModel
import com.ntg.stepcounter.vm.StepViewModel
import com.ntg.stepcounter.vm.UserDataViewModel
import kotlinx.coroutines.launch


@Composable
fun SignInScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel,
    userDataViewModel: UserDataViewModel,
    socialNetworkViewModel: SocialNetworkViewModel,
    stepViewModel: StepViewModel,
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
            Content(paddingValues = innerPadding, navHostController = navHostController, loginViewModel, userDataViewModel, socialNetworkViewModel,stepViewModel, state, phoneNumber)
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, navHostController: NavHostController, loginViewModel: LoginViewModel, userDataViewModel: UserDataViewModel,socialNetworkViewModel: SocialNetworkViewModel,stepViewModel: StepViewModel, state: String?, phoneNumber: String?){

    val uid = remember {
        mutableStateOf("")
    }

    val owner = LocalLifecycleOwner.current
    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 32.dp)) {

        EditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            label = stringResource(id = if (state.orEmpty() == "1") R.string.student_id else R.string.prof_id),
            text = uid,
            keyboardType = KeyboardType.Number
        )

        CustomButton(modifier = Modifier.padding(top = 16.dp).fillMaxWidth(), text = stringResource(id = R.string.sign_in), size = ButtonSize.XL){

            if (uid.value.isNotEmpty()){
                userDataViewModel.signIn(uid.value, phoneNumber.orEmpty()).observe(owner){

                    when(it){
                        is NetworkResult.Error -> {

                        }
                        is NetworkResult.Loading -> {

                        }
                        is NetworkResult.Success -> {

                            if (it.data?.isSuccess.orFalse()){
                                stepViewModel.insertAll(it.data?.data?.stepsList.orEmpty().map { Step(id=0, date = it.date, start = 0, count = it.steps.orZero(),synced = it.steps) })
                                socialNetworkViewModel.insertAll(it.data?.data?.socials.orEmpty().map { Social(id=0, name = it.title.orEmpty(), pageId = it.url.orEmpty()) })
                                userDataViewModel.setUserStatus(state.orEmpty())
                                userDataViewModel.setFieldStudy(it.data?.data?.fosName.orEmpty())
                                userDataViewModel.setUserId(uid.value)
                                userDataViewModel.setPhone(phoneNumber.orEmpty())
                                userDataViewModel.setUsername(it.data?.data?.fullName.orEmpty())
                                userDataViewModel.setGradeId(it.data?.data?.gradeId.orZero())
                                userDataViewModel.setFosId(it.data?.data?.fosId.orZero())

                            }else{
                                context.toast(context.getString(R.string.wrong_info))
                            }

                        }
                    }

                }
            }else{
                context.toast(context.getString(if (state == "1") R.string.student_id_empty else R.string.prof_id_empty))
            }

        }
    }



}