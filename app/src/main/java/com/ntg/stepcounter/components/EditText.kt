package com.ntg.stepcounter.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.ui.theme.SECONDARY500
import com.ntg.stepcounter.ui.theme.SECONDARY900
import com.ntg.stepcounter.ui.theme.fontMedium14
import com.ntg.stepcounter.ui.theme.fontRegular12
import com.ntg.stepcounter.ui.theme.fontRegular14

@Composable
fun EditText(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: MutableState<String> = remember { mutableStateOf("") },
    setError: MutableState<Boolean> = remember { mutableStateOf(false) },
    label: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    leadingIcon: ImageVector = Icons.Rounded.Add,
    enabledLeadingIcon: Boolean = false,
    leadingIconOnClick: (String) -> Unit = {},
    onChange: (String) -> Unit = {},
    onClick: () -> Unit = {}

) {


    var passwordVisible by rememberSaveable { mutableStateOf(true) }

    OutlinedTextField(
        modifier = modifier
        .width(2.dp)
        .clickable {
            onClick.invoke()
        },
        value = text.value,
        onValueChange = {
            text.value = it
            onChange.invoke(it)
        },
        label = {
            if (!label.isNullOrEmpty()) {
                Text(text = label, style = fontRegular12(SECONDARY500))
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        readOnly = readOnly,
        textStyle = fontMedium14(SECONDARY900),
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
    )




}