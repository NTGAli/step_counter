package com.ntg.stepi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.ntg.stepi.util.extension.orFalse
import com.ntg.stepi.util.extension.orZero
import com.ntg.stepi.util.extension.toast
import com.ntg.stepi.util.extension.validLength
import com.ntg.stepi.util.extension.validUsername
import com.ntg.stepi.vm.LoginViewModel
import com.ntg.stepi.vm.UserDataViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OtherRegister(
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

    val password = rememberSaveable {
        mutableStateOf("")
    }

    val job = rememberSaveable {
        mutableStateOf("")
    }

    val jobId = rememberSaveable {
        mutableStateOf(0)
    }

    var applied by rememberSaveable {
        mutableStateOf(false)
    }

    var isVerified by rememberSaveable {
        mutableStateOf(false)
    }

    var usernameError = rememberSaveable {
        mutableStateOf(false)
    }

    val loading = remember {
        mutableStateOf(false)
    }
    job.value = loginViewModel.job?.title.orEmpty()
    jobId.value = loginViewModel.job?.id.orZero()


    isVerified = userDataViewModel.isVerified().collectAsState(initial = false).value


    val jobData = FieldOfStudy()

    if (edit.orFalse() && !applied) {

        userDataViewModel.getUsername().collectAsState(initial = "").value.let {
            fullName.value = it
        }

        userDataViewModel.getUserId().collectAsState(initial = "").value.let {
            password.value = it
        }

        userDataViewModel.getFieldStudy().collectAsState(initial = "").value.let {
            job.value = it
            jobData.title = it
            loginViewModel.job = jobData
        }

        userDataViewModel.getFosId().collectAsState(initial = -1).value.let {
            jobId.value = it
            jobData.id = it
            loginViewModel.job = jobData
        }

        if (fullName.value.isNotEmpty() && fullName.value.isNotEmpty() && jobId.value != -1 && loginViewModel.job != null && loginViewModel.job?.id != null) {
            applied = true
        }

    }


    LazyColumn {


        item {
            if (edit.orFalse()) {
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
                    label = stringResource(id = R.string.username),
                    keyboardType = KeyboardType.Text,
                    text = password,
                    setError = usernameError,
                    errorMessage = ctx.getString(R.string.username_exist),
                    enabled = !(isVerified && edit.orFalse()),
                    onChange = {
                        usernameError.value = false
                    }
                )


                EditText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = stringResource(id = R.string.job),
                    text = job,
                    readOnly = true
                ) {
                    navHostController.navigate(Screens.JobScreen.name)
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
                                    password.value,
                                    ctx.getString(R.string.password_id_empty)
                                )
                            }
                            .then {
                                notEmptyOrNull(
                                    job.value,
                                    ctx.getString(R.string.job_empty)
                                )
                            }
                            .then {
                                validUsername(
                                    password.value,
                                    ctx.getString(R.string.invalid_username)
                                )
                            }
                            .then {
                                validLength(
                                    password.value,
                                    5,
                                    ctx.getString(R.string.most_more_5_char)
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
                                    phone = phoneNumber.orEmpty(),
                                    fullName = fullName.value.trim(),
                                    fosId = jobId.value.toString(),
                                    uid = password.value,
                                    state = "4",
                                    gradeId = "4"
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
                                                userDataViewModel.setUserStatus("4")
                                                userDataViewModel.setFieldStudy(loginViewModel.job?.title.orEmpty())
                                                userDataViewModel.setUserId(password.value)
                                                userDataViewModel.setPhone(phoneNumber.orEmpty())
                                                userDataViewModel.setFosId(loginViewModel.job?.id.orZero())
                                                navHostController.navigate(Screens.HomeScreen.name) {
                                                    popUpTo(0)
                                                }
                                            } else {
                                                usernameError.value = true
                                            }
                                        }
                                    }

                                }
                            } else {
                                loginViewModel.register(
                                    phone = phoneNumber.orEmpty(),
                                    fullName = fullName.value.trim(),
                                    fosId = jobId.value.toString(),
                                    uid = password.value,
                                    state = "4",
                                    gradeId = "4",
                                    timeSign = System.currentTimeMillis().toString()
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
                                                userDataViewModel.setTimeSign(it.data?.data.orEmpty())
                                                userDataViewModel.setUsername(fullName.value.trim())
                                                userDataViewModel.setClaps(0)
                                                userDataViewModel.setUserStatus("4")
                                                userDataViewModel.setFieldStudy(loginViewModel.job?.title.orEmpty())
                                                userDataViewModel.setUserId(password.value)
                                                userDataViewModel.setPhone(phoneNumber.orEmpty())
                                                userDataViewModel.setFosId(loginViewModel.job?.id.orZero())
                                                navHostController.navigate(Screens.HomeScreen.name) {
                                                    popUpTo(0)
                                                }
                                            } else {
                                                usernameError.value = true
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
