package com.ntg.stepi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
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
import com.ntg.stepi.components.SampleItem
import com.ntg.stepi.models.Failure
import com.ntg.stepi.models.FieldOfStudy
import com.ntg.stepi.models.Success
import com.ntg.stepi.models.components.GradeItem
import com.ntg.stepi.models.then
import com.ntg.stepi.nav.Screens
import com.ntg.stepi.ui.theme.PRIMARY100
import com.ntg.stepi.ui.theme.PRIMARY900
import com.ntg.stepi.ui.theme.TERTIARY100
import com.ntg.stepi.ui.theme.TERTIARY900
import com.ntg.stepi.ui.theme.backgroundScrim
import com.ntg.stepi.ui.theme.fontMedium12
import com.ntg.stepi.util.extension.notEmptyOrNull
import com.ntg.stepi.util.extension.notNull
import com.ntg.stepi.util.extension.orFalse
import com.ntg.stepi.util.extension.orZero
import com.ntg.stepi.util.extension.toast
import com.ntg.stepi.util.extension.validLength
import com.ntg.stepi.vm.LoginViewModel
import com.ntg.stepi.vm.UserDataViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RegisterScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel,
    userDataViewModel: UserDataViewModel,
    phoneNumber: String?,
    edit: Boolean?
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val scope = rememberCoroutineScope()
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

    val loading = remember {
        mutableStateOf(false)
    }

    var applied by rememberSaveable {
        mutableStateOf(false)
    }

    var isVerified by rememberSaveable {
        mutableStateOf(false)
    }

    fosId.value = loginViewModel.fieldOfStudy?.title.orEmpty()


    val gradeItems = listOf(
        GradeItem(
            1,
            stringResource(id = R.string.bachelor)
        ),
        GradeItem(
            2,
            stringResource(id = R.string.master)
        ),
        GradeItem(
            3,
            stringResource(id = R.string.phd)
        )
    )

    val grade = rememberSaveable {
        mutableStateOf("")
    }

    val uidError = remember {
        mutableStateOf(false)
    }

    isVerified = userDataViewModel.isVerified().collectAsState(initial = false).value


    if (edit.orFalse() && !applied) {

        val fieldStudy = FieldOfStudy()

        userDataViewModel.getUsername().collectAsState(initial = "").value.let {
            fullName.value = it
        }

        userDataViewModel.getUserId().collectAsState(initial = "").value.let {
            sId.value = it
        }

        userDataViewModel.getGradeId().collectAsState(initial = -1).value.let { gId ->
            gradeId.value = gId
            try {
                grade.value = gradeItems.first { it.id == gId }.title
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        userDataViewModel.getFosId().collectAsState(initial = null).value.let {
            if (it != null) {
                fieldStudy.id = it
                loginViewModel.fieldOfStudy = fieldStudy
            }
        }

        userDataViewModel.getFieldStudy().collectAsState(initial = "").value.let {
            fieldStudy.title = it
            loginViewModel.fieldOfStudy = fieldStudy
        }

        if (fullName.value.isNotEmpty() && loginViewModel.fieldOfStudy != null && loginViewModel.fieldOfStudy?.id != null) {
            applied = true
        }

    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = MaterialTheme.colors.onBackground,
        scrimColor = backgroundScrim,
        sheetContent = {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .width(32.dp)
                        .height(4.dp)
                        .background(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colors.secondary
                        )
                )

                LazyColumn(modifier = Modifier.background(MaterialTheme.colors.onBackground)) {
                    items(gradeItems) {
                        SampleItem(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            title = it.title,
                            id = it.id
                        ) { _, id, _ ->
                            gradeId.value = id.orZero()
                            grade.value = try {
                                gradeItems.first { it.id == id.orZero() }.title
                            } catch (e: Exception) {
                                ""
                            }
                            scope.launch {
                                sheetState.hide()
                            }
                        }
                    }
                }
            }


        },
        sheetShape = RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp)
    ) {

        LazyColumn {
            item {
                Appbar(
                    title = if (edit.orFalse()) stringResource(R.string.edit_account_info) else stringResource(
                        R.string.register
                    ),
                    navigationOnClick = { navHostController.popBackStack() }
                )
            }

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
                Column(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .padding(top = 16.dp)
                ) {

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
                        label = stringResource(id = R.string.student_id),
                        keyboardType = KeyboardType.Number, text = sId,
                        enabled = !(isVerified && edit.orFalse()),
                        setError = uidError,
                        errorMessage = ctx.getString(R.string.uid_already_exist),
                        onChange = {
                            uidError.value = false
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

                    EditText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = stringResource(id = R.string.grade),
                        text = grade,
                        readOnly = true
                    ) {
                        scope.launch {
                            sheetState.show()
                        }
                    }

                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        text = if (edit.orFalse()) stringResource(id = R.string.save) else stringResource(
                            id = R.string.next
                        ),
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
                                .then {
                                    notEmptyOrNull(
                                        gradeId.intValue,
                                        ctx.getString(R.string.grade_id_empty)
                                    )
                                }
                                .then {
                                    validLength(
                                        sId.value,
                                        5,
                                        ctx.getString(R.string.grad_most_more_5_char)
                                    )
                                }

                        when (result) {
                            is Failure -> {
                                ctx.toast(result.errorMessage)
                            }

                            is Success -> {
                                loading.value = true

                                if (edit.orFalse()) {


                                    loginViewModel.editUserDate(
                                        phone = phoneNumber.orEmpty(),
                                        fullName = fullName.value.trim(),
                                        state = "1",
                                        uid = sId.value,
                                        fosId = loginViewModel.fieldOfStudy?.id.toString(),
                                        gradeId = gradeId.value.toString()
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
                                                    userDataViewModel.setUserStatus("1")
                                                    userDataViewModel.setFieldStudy(loginViewModel.fieldOfStudy?.title.orEmpty())
                                                    userDataViewModel.setUserId(sId.value)
                                                    userDataViewModel.setPhone(phoneNumber.orEmpty())
                                                    userDataViewModel.setUsername(fullName.value.trim())
                                                    userDataViewModel.setGradeId(gradeId.value)
                                                    userDataViewModel.setFosId(loginViewModel.fieldOfStudy?.id.orZero())
                                                    ctx.toast(ctx.getString(R.string.user_updated_successfully))
                                                    navHostController.popBackStack(
                                                        Screens.SettingsScreen.name,
                                                        false
                                                    )
                                                } else if (it.data?.message == "UID_ALREADY_EXIST") {
                                                    uidError.value = true
                                                } else {
                                                    ctx.toast(ctx.getString(R.string.user_not_updated))
                                                }
                                            }
                                        }

                                    }
                                } else {
                                    loginViewModel.register(
                                        phoneNumber.orEmpty(),
                                        fullName.value.trim(),
                                        "1",
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
                                                if (it.data?.isSuccess.orFalse()) {
                                                    userDataViewModel.setTimeSign(it.data?.data.orEmpty())
                                                    userDataViewModel.setUserStatus("1")
                                                    userDataViewModel.setClaps(0)
                                                    userDataViewModel.setFieldStudy(loginViewModel.fieldOfStudy?.title.orEmpty())
                                                    userDataViewModel.setUserId(sId.value)
                                                    userDataViewModel.setPhone(phoneNumber.orEmpty())
                                                    userDataViewModel.setUsername(fullName.value.trim())
                                                    userDataViewModel.setGradeId(gradeId.value)
                                                    userDataViewModel.setFosId(loginViewModel.fieldOfStudy?.id.orZero())
                                                } else if (it.data?.message == "UID_ALREADY_EXIST") {
                                                    uidError.value = true
                                                } else {
                                                    ctx.toast(ctx.getString(R.string.user_not_registered))
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
}