package com.ntg.stepi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.stepi.ui.theme.fontMedium14
import com.ntg.stepi.ui.theme.fontRegular12

@Composable
fun EditText(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: MutableState<String> = remember { mutableStateOf("") },
    setError: MutableState<Boolean> = remember { mutableStateOf(false) },
    errorMessage: String? = null,
    label: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    leadingIcon: ImageVector = Icons.Rounded.Add,
    enabledLeadingIcon: Boolean = false,
    maxLength: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIconOnClick: (String) -> Unit = {},
    onChange: (String) -> Unit = {},
    onClick: () -> Unit = {}

) {


    var passwordVisible by rememberSaveable { mutableStateOf(true) }

    Column {
        OutlinedTextField(
            modifier = modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onClick.invoke()
                },

            value = text.value,
            onValueChange = {
                if (maxLength != null && it.length > maxLength) return@OutlinedTextField
                text.value = it
                onChange.invoke(it)
            },
            label = {
                if (!label.isNullOrEmpty()) {
                    Text(text = label, style = if (enabled) fontRegular12(MaterialTheme.colors.secondary) else fontRegular12(
                        MaterialTheme.colors.onSurface))
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            readOnly = readOnly,
            textStyle = if (enabled)fontMedium14(MaterialTheme.colors.surface) else fontMedium14(MaterialTheme.colors.secondary),
            enabled = enabled,
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                if (enabledLeadingIcon) {
                    IconButton(onClick = {
                        leadingIconOnClick.invoke(text.value)
                    }) {
                        Icon(
                            imageVector = leadingIcon, contentDescription = "leading"
                        )
                    }
                } else if (isPassword) {
                    val image = if (passwordVisible) Icons.Rounded.Visibility
                    else Icons.Filled.VisibilityOff

                    // Please provide localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }


            },
            interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            onClick.invoke()
                        }
                    }
                }
            }
            ,
            isError = setError.value,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colors.onSurface

            )

        )

        if (errorMessage != null){
            Text(modifier = Modifier.padding(top = 4.dp),text = errorMessage, style = fontRegular12(MaterialTheme.colors.error))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditTextPreview(){
    val error = remember {
        mutableStateOf(false)
    }
    EditText(modifier = Modifier.fillMaxWidth(), label = "test", setError = error, errorMessage = "test error text", enabled = true)
}