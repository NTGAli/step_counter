package com.ntg.stepcounter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.ntg.stepcounter.components.SampleItem
import com.ntg.stepcounter.models.Failure
import com.ntg.stepcounter.models.FieldOfStudy
import com.ntg.stepcounter.models.Success
import com.ntg.stepcounter.models.components.AppbarItem
import com.ntg.stepcounter.models.components.GradeItem
import com.ntg.stepcounter.models.then
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.SECONDARY200
import com.ntg.stepcounter.util.extension.notEmptyOrNull
import com.ntg.stepcounter.util.extension.notNull
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.util.extension.toast
import com.ntg.stepcounter.vm.LoginViewModel
import com.ntg.stepcounter.vm.UserDataViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfRegisterScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel,
    userDataViewModel: UserDataViewModel,
    phoneNumber: String?,
    edit: Boolean,
) {
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.register),
                navigationOnClick = { navHostController.popBackStack() },
                actions = listOf(
                    AppbarItem(
                        id = 0,
                        imageVector = Icons.Rounded.Search
                    )
                ),
            )
        },
        content = { innerPadding ->
            Content(innerPadding, navHostController, loginViewModel, userDataViewModel, phoneNumber, edit)
        }
    )

}

@Composable
private fun Content(innerPaddingValues: PaddingValues, navHostController: NavHostController, loginViewModel: LoginViewModel, userDataViewModel: UserDataViewModel, phoneNumber: String?,edit: Boolean) {

    val ctx = LocalContext.current
    val owner = LocalLifecycleOwner.current

    val fullName = rememberSaveable {
        mutableStateOf("")
    }

    val sId = rememberSaveable {
        mutableStateOf("")
    }

    val gradeId = rememberSaveable {
        mutableIntStateOf(-1)
    }

    val fosId = rememberSaveable {
        mutableStateOf("")
    }

    var applied by rememberSaveable {
        mutableStateOf(false)
    }

    var isVerified by rememberSaveable {
        mutableStateOf(false)
    }

    val loading = remember {
        mutableStateOf(false)
    }

    fosId.value = loginViewModel.fieldOfStudy?.title.orEmpty()
    isVerified = userDataViewModel.isVerified().collectAsState(initial = false).value


    if (edit.orFalse() && !applied){

        val fieldStudy = FieldOfStudy()

        userDataViewModel.getUsername().collectAsState(initial = "").value.let {
            fullName.value = it
        }

        userDataViewModel.getUserId().collectAsState(initial = "").value.let {
            sId.value = it
        }

        userDataViewModel.getFosId().collectAsState(initial = -1).value.let {
            fieldStudy.id = it
            loginViewModel.fieldOfStudy = fieldStudy
        }

        userDataViewModel.getFieldStudy().collectAsState(initial = "").value.let {
            fieldStudy.title = it
            loginViewModel.fieldOfStudy = fieldStudy
        }

        if (fullName.value.isNotEmpty() && loginViewModel.fieldOfStudy != null && fieldStudy.id != -1){
            applied = true
        }

    }


    LazyColumn {

        item {
            Column(modifier = Modifier.padding(horizontal = 32.dp)) {

                EditText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    label = stringResource(id = R.string.full_name),
                    text = fullName
                )

                EditText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = stringResource(id = R.string.prof_id),
                    keyboardType = KeyboardType.Number, text = sId,
                    enabled = !(isVerified && edit.orFalse())
                )

                EditText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = stringResource(id = R.string.field_study),
                    text = fosId,
                    readOnly = true
                ) {
                    navHostController.navigate(Screens.FieldStudiesScreen.name)
                }



                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    text = if (edit) stringResource(id = R.string.save) else stringResource(id = R.string.next),
                    size = ButtonSize.XL,
                    loading = loading.value
                ) {

                    val result =
                        notEmptyOrNull(fullName.value, ctx.getString(R.string.full_name_empty))
                            .then {
                                notEmptyOrNull(
                                    sId.value,
                                    ctx.getString(R.string.student_id_empty)
                                )
                            }
                            .then {
                                notNull(
                                    loginViewModel.fieldOfStudy,
                                    ctx.getString(R.string.fos_empty)
                                )
                            }


                    when (result) {
                        is Failure -> {
                            ctx.toast(result.errorMessage)
                        }

                        is Success -> {
                            loading.value = true

                            if (edit){
                                loginViewModel.editUserDate(
                                    phoneNumber.orEmpty(),
                                    fullName.value,
                                    "2",
                                    sId.value,
                                    loginViewModel.fieldOfStudy?.id.toString(),
                                    gradeId.value.toString()
                                ).observe(owner) {
                                    when (it) {
                                        is NetworkResult.Error -> {
                                            ctx.getString(R.string.sth_wrong)
                                            loading.value = false
                                        }

                                        is NetworkResult.Loading -> {
                                        }

                                        is NetworkResult.Success -> {
                                            userDataViewModel.setUsername(fullName.value)
                                            userDataViewModel.setUserStatus("2")
                                            userDataViewModel.setFieldStudy(loginViewModel.fieldOfStudy?.title.orEmpty())
                                            userDataViewModel.setUserId(sId.value)
                                            userDataViewModel.setPhone(phoneNumber.orEmpty())
                                            navHostController.navigate(Screens.HomeScreen.name) {
                                                popUpTo(0)
                                            }
                                        }
                                    }

                                }
                            }else{
                                loginViewModel.register(
                                    phoneNumber.orEmpty(),
                                    fullName.value,
                                    "2",
                                    sId.value,
                                    loginViewModel.fieldOfStudy?.id.toString(),
                                    gradeId.value.toString()
                                ).observe(owner) {
                                    when (it) {
                                        is NetworkResult.Error -> {
                                            ctx.getString(R.string.sth_wrong)
                                            loading.value = false
                                        }

                                        is NetworkResult.Loading -> {
                                        }

                                        is NetworkResult.Success -> {
                                            userDataViewModel.setUsername(fullName.value)
                                            userDataViewModel.setUserStatus("2")
                                            userDataViewModel.setFieldStudy(loginViewModel.fieldOfStudy?.title.orEmpty())
                                            userDataViewModel.setUserId(sId.value)
                                            userDataViewModel.setPhone(phoneNumber.orEmpty())
                                            navHostController.navigate(Screens.HomeScreen.name) {
                                                popUpTo(0)
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }

                if (!isVerified && edit.orFalse()){
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        text = stringResource(id = R.string.im_student),
                        size = ButtonSize.XL,
                        style = ButtonStyle.TextOnly
                    ) {
                        if (navHostController.previousBackStackEntry?.destination?.route == Screens.SettingsScreen.name){
                            navHostController.navigate(Screens.RegisterScreen.name + "?phone=$phoneNumber&edit=${true}")
                        }else{
                            navHostController.popBackStack()
                        }
                    }
                }
            }

        }
    }
}