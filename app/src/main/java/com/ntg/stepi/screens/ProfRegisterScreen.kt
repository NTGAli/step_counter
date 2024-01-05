package com.ntg.stepi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.stepi.R
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.components.CustomButton
import com.ntg.stepi.components.EditText
import com.ntg.stepi.models.Failure
import com.ntg.stepi.models.FieldOfStudy
import com.ntg.stepi.models.Success
import com.ntg.stepi.models.then
import com.ntg.stepi.nav.Screens
import com.ntg.stepi.ui.theme.PRIMARY100
import com.ntg.stepi.ui.theme.PRIMARY900
import com.ntg.stepi.ui.theme.TERTIARY100
import com.ntg.stepi.ui.theme.TERTIARY900
import com.ntg.stepi.ui.theme.fontMedium12
import com.ntg.stepi.util.extension.notEmptyOrNull
import com.ntg.stepi.util.extension.notNull
import com.ntg.stepi.util.extension.orFalse
import com.ntg.stepi.util.extension.toast
import com.ntg.stepi.vm.LoginViewModel
import com.ntg.stepi.vm.UserDataViewModel

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
            )
        },
        content = { innerPadding ->
            Content(
                innerPadding,
                navHostController,
                loginViewModel,
                userDataViewModel,
                phoneNumber,
                edit
            )
        }
    )

}

@Composable
private fun Content(
    innerPaddingValues: PaddingValues,
    navHostController: NavHostController,
    loginViewModel: LoginViewModel,
    userDataViewModel: UserDataViewModel,
    phoneNumber: String?,
    edit: Boolean
) {

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

    val sidError = remember {
        mutableStateOf(false)
    }

    fosId.value = loginViewModel.fieldOfStudy?.title.orEmpty()
    isVerified = userDataViewModel.isVerified().collectAsState(initial = false).value


    if (edit.orFalse() && !applied) {

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

        if (fullName.value.isNotEmpty() && loginViewModel.fieldOfStudy != null) {
            applied = true
        }

    }


    LazyColumn {


        item {
            if (edit.orFalse()){
                Box(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isVerified) PRIMARY100 else TERTIARY100)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                        text = if (isVerified) stringResource(id = R.string.account_verified) else stringResource(
                            id = R.string.account_pending
                        ),
                        style = fontMedium12(
                            if (isVerified) PRIMARY900 else TERTIARY900
                        )
                    )
                }
            }
        }

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
                    enabled = !(isVerified && edit.orFalse()),
                    setError = sidError,
                    errorMessage = ctx.getString(R.string.pid_already_exist),
                    onChange = {
                        sidError.value = false
                    }
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
                                    ctx.getString(R.string.prof_id_empty)
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

                            if (edit) {


                                loginViewModel.editUserDate(
                                    phoneNumber.orEmpty(),
                                    fullName.value.trim(),
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
                                            loading.value = false
                                            if (it.data?.isSuccess.orFalse()) {
                                                userDataViewModel.setUsername(fullName.value.trim())
                                                userDataViewModel.setUserStatus("2")
                                                userDataViewModel.setFieldStudy(loginViewModel.fieldOfStudy?.title.orEmpty())
                                                userDataViewModel.setUserId(sId.value)
                                                userDataViewModel.setPhone(phoneNumber.orEmpty())
                                                navHostController.navigate(Screens.HomeScreen.name) {
                                                    popUpTo(0)
                                                }
                                            }else{
                                                sidError.value = true
                                            }

                                        }
                                    }

                                }
                            } else {
                                loginViewModel.register(
                                    phoneNumber.orEmpty(),
                                    fullName.value.trim(),
                                    "2",
                                    sId.value,
                                    loginViewModel.fieldOfStudy?.id.toString(),
                                    gradeId.value.toString(),
                                    System.currentTimeMillis().toString()
                                ).observe(owner) {
                                    when (it) {
                                        is NetworkResult.Error -> {
                                            ctx.getString(R.string.sth_wrong)
                                            loading.value = false
                                        }

                                        is NetworkResult.Loading -> {
                                        }

                                        is NetworkResult.Success -> {
                                            loading.value = false
                                            if (it.data?.isSuccess.orFalse()){
                                                userDataViewModel.setTimeSign(it.data?.data.orEmpty())
                                                userDataViewModel.setUsername(fullName.value.trim())
                                                userDataViewModel.setClaps(0)
                                                userDataViewModel.setUserStatus("2")
                                                userDataViewModel.setFieldStudy(loginViewModel.fieldOfStudy?.title.orEmpty())
                                                userDataViewModel.setUserId(sId.value)
                                                userDataViewModel.setPhone(phoneNumber.orEmpty())
                                                navHostController.navigate(Screens.HomeScreen.name) {
                                                    popUpTo(0)
                                                }
                                            }else{
                                                sidError.value = true
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }

        }
    }
}