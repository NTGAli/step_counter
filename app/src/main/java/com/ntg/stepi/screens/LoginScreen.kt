package com.ntg.stepi.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.stepi.R
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.components.CustomButton
import com.ntg.stepi.components.EditText
import com.ntg.stepi.nav.Screens
import com.ntg.stepi.ui.theme.PRIMARY500
import com.ntg.stepi.ui.theme.SECONDARY500
import com.ntg.stepi.ui.theme.fontMedium12
import com.ntg.stepi.util.extension.isValidIranMobileNumber
import com.ntg.stepi.util.extension.orFalse
import com.ntg.stepi.util.extension.timber
import com.ntg.stepi.util.extension.toast
import com.ntg.stepi.vm.LoginViewModel
import com.ntg.stepi.vm.UserDataViewModel
import java.util.Locale

@Composable
fun LoginScreen(
    navHostController: NavHostController,
    loginViewModel: LoginViewModel,
    userDataViewModel: UserDataViewModel
) {
    Scaffold(
        topBar = {
            Appbar(
                showLogo = true,
                enableNavigation = false
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navHostController, loginViewModel, userDataViewModel)
        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navHostController: NavHostController,
    loginViewModel: LoginViewModel,
    userDataViewModel: UserDataViewModel
) {

    val owner = LocalLifecycleOwner.current
    val ctx = LocalContext.current

    val phoneNumber = remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    CheckLanguage(userDataViewModel)

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            label = stringResource(id = R.string.phone_number),
            text = phoneNumber,
            maxLength = 11,
            keyboardType = KeyboardType.Number
        )

        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = stringResource(id = R.string.next),
            size = ButtonSize.XL,
            loading = loading
        ) {

            if (phoneNumber.value.isEmpty()) {
                ctx.toast(ctx.getString(R.string.phone_can_not_empty))
                return@CustomButton
            } else if (phoneNumber.value.isValidIranMobileNumber()) {
                ctx.toast(ctx.getString(R.string.wrong_format_phone))
                return@CustomButton
            }
            if (phoneNumber.value.isNotEmpty()) {
                loginViewModel.login(phoneNumber.value).observe(owner) {

                    when (it) {
                        is NetworkResult.Error -> {
                            loading = false
                            ctx.toast(ctx.getString(R.string.sth_wrong))
                            timber("LoginERR ::: ${it.message.orEmpty()}")
                        }

                        is NetworkResult.Loading -> {
                            loading = true
                        }

                        is NetworkResult.Success -> {
                            loading = false

                            if (it.data?.isSuccess.orFalse()) {
                                if (it.data?.message.orEmpty() == "-1") {
                                    navHostController.navigate(Screens.RoleScreen.name + "?phone=${phoneNumber.value}")
                                } else {
                                    navHostController.navigate(Screens.SignInScreen.name + "?phone=${phoneNumber.value}&state=${it.data?.message.orEmpty()}")
                                }
                            } else {
                                ctx.toast(ctx.getString(R.string.sth_wrong))
                            }
                        }
                    }

                }
            }

        }

        TextWithLink(navHostController = navHostController)
    }

}

@Composable
private fun CheckLanguage(userDataViewModel: UserDataViewModel) {
    if (Locale.getDefault().language == "ar"){
        userDataViewModel.setLanguage("ar")
    }else{
        userDataViewModel.setLanguage("fa")
    }
}

@Composable
private fun TextWithLink(
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {

    val annotatedString = buildAnnotatedString {
        append(stringResource(R.string.privacy_first_part))

        pushStringAnnotation(tag = "policy", annotation = "policy")
        withStyle(style = SpanStyle(color = PRIMARY500)) {
            append(stringResource(id = R.string.privacy_policies))
        }
        pop()

        append(stringResource(R.string.and_format))

        pushStringAnnotation(tag = "terms", annotation = "terms")
        withStyle(style = SpanStyle(color = PRIMARY500)) {
            append(stringResource(id = R.string.terms_and_conditions))
        }

        pop()

        append(stringResource(R.string.agree_privacy))

    }

    ClickableText(modifier = modifier.padding(top = 24.dp),text = annotatedString, style = fontMedium12(SECONDARY500), onClick = { offset ->
        annotatedString.getStringAnnotations(tag = "policy", start = offset, end = offset)
            .firstOrNull()?.let {
            navHostController.navigate(Screens.PrivacyPolicyScreen.name)
        }

        annotatedString.getStringAnnotations(tag = "terms", start = offset, end = offset)
            .firstOrNull()?.let {
                navHostController.navigate(Screens.TermAndConditionsScreen.name)
        }
    })

//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(top = 24.dp).padding(horizontal = 16.dp)
//            .background(SECONDARY200),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        Text(text = "درصورت ادامه با", style = fontMedium12(SECONDARY500))
//        Text(modifier = Modifier
//            .clip(RoundedCornerShape(4.dp))
//            .clickable {
//                navHostController.navigate(Screens.TermAndConditionsScreen.name)
//            }, text = " قوانین و مقررات ", style = fontMedium12(PRIMARY500))
//        Text(text = "و", style = fontMedium12(SECONDARY500))
//        Text(modifier = Modifier
//            .clip(RoundedCornerShape(4.dp))
//            .clickable {
//                navHostController.navigate(Screens.PrivacyPolicyScreen.name)
//            }, text = " سیاست های حریم خصوصی ", style = fontMedium12(PRIMARY500))
//        Text(text = "موافقت می کنید.", style = fontMedium12(SECONDARY500))
//
//    }
}

