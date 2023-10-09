package com.ntg.stepcounter.screens

import android.content.Context
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
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
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
import com.ntg.stepcounter.models.Success
import com.ntg.stepcounter.models.components.GradeItem
import com.ntg.stepcounter.models.then
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.SECONDARY200
import com.ntg.stepcounter.util.extension.notEmptyOrNull
import com.ntg.stepcounter.util.extension.notNull
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.util.extension.toast
import com.ntg.stepcounter.vm.LoginViewModel
import com.ntg.stepcounter.vm.UserDataViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RegisterScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel,
    userDataViewModel: UserDataViewModel,
    phoneNumber: String?
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

    timber("dajwdhwajkhdkjwd ${grade.value}")

    ModalBottomSheetLayout(sheetState = sheetState, sheetContent = {

        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Box(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .background(shape = RoundedCornerShape(16.dp), color = SECONDARY200)
            )

            LazyColumn{
                items(gradeItems){
                    SampleItem(modifier = Modifier.padding(horizontal = 24.dp), title = it.title, id = it.id){ _, id, _ ->
                        gradeId.value = id.orZero()
                        grade.value = try { gradeItems.first { it.id == id.orZero()}.title }catch (e: Exception) {""}
                        scope.launch {
                            sheetState.hide()
                        }
                    }
                }
            }
        }


    }, sheetShape = RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp)) {

        LazyColumn{
            item {
                Appbar(
                    title = stringResource(R.string.register),
                    navigationOnClick = { navHostController.popBackStack() }
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 32.dp)) {

                    EditText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp), label = stringResource(id = R.string.full_name), text = fullName
                    )

                    EditText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = stringResource(id = R.string.student_id),
                        keyboardType = KeyboardType.Number, text = sId
                    )

                    EditText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp), label = stringResource(id = R.string.field_study), text = fosId, readOnly = true
                    ){
                        navHostController.navigate(Screens.FieldStudiesScreen.name)
                    }

                    EditText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp), label = stringResource(id = R.string.grade), text = grade, readOnly = true
                    ){
                        scope.launch {
                            sheetState.show()
                        }
                    }

                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        text = stringResource(id = R.string.next),
                        size = ButtonSize.XL,
                        loading = loading.value
                    ) {

                        var result = notEmptyOrNull(fullName.value, ctx.getString(R.string.full_name_empty))
                            .then { notEmptyOrNull(sId.value, ctx.getString(R.string.student_id_empty)) }
                            .then { notNull(loginViewModel.fieldOfStudy, ctx.getString(R.string.fos_empty)) }
                            .then { notEmptyOrNull(gradeId.value, ctx.getString(R.string.student_id)) }

                        when (result){
                            is Failure -> {
                                ctx.toast(result.errorMessage)
                            }
                            is Success -> {
                                loading.value = true
                                loginViewModel.register(phoneNumber.orEmpty(), fullName.value, "1", sId.value, loginViewModel.fieldOfStudy?.id.toString(), gradeId.value.toString()).observe(owner){
                                    when (it){
                                        is NetworkResult.Error -> {
                                            ctx.getString(R.string.sth_wrong)
                                            loading.value = false
                                        }
                                        is NetworkResult.Loading -> {
                                        }
                                        is NetworkResult.Success -> {
                                            userDataViewModel.setUsername(fullName.value)
                                            userDataViewModel.setUserStatus("1")
                                            userDataViewModel.setFieldStudy(loginViewModel.fieldOfStudy?.title.orEmpty())
                                            userDataViewModel.setUserId(sId.value)
                                            userDataViewModel.setPhone(phoneNumber.orEmpty())
                                            navHostController.navigate(Screens.HomeScreen.name){
                                                popUpTo(0)
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        text = stringResource(id = R.string.im_prof),
                        size = ButtonSize.XL,
                        style = ButtonStyle.TextOnly
                    ) {
                        navHostController.navigate(Screens.ProfRegisterScreen.name + "?phone=$phoneNumber")
                    }
                }

            }
        }


    }
}