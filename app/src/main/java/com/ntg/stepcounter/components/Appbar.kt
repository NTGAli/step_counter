package com.ntg.stepcounter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ntg.stepcounter.models.components.AppbarItem
import com.ntg.stepcounter.models.components.PopupItem
import com.ntg.stepcounter.ui.theme.*

@Composable
fun Appbar(
    modifier: Modifier = Modifier,
//    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "Appbar",
    titleColor: Color = SECONDARY900,
    color: Color = Color.White,
    enableNavigation: Boolean = true,
    navigationOnClick: () -> Unit = {},
    navigateIconColor: Color = SECONDARY500,
    enableSearchbar: MutableState<Boolean> = remember { mutableStateOf(false) },
    searchQueryText: MutableState<String> = remember { mutableStateOf("") },
    actions: List<AppbarItem> = emptyList(),
    popupItems: List<PopupItem> = emptyList(),
    actionOnClick: (Int) -> Unit = {},
    popupItemOnClick: (Int) -> Unit = {},
    onQueryChange: (String) -> Unit = {}
) {
    if (enableSearchbar.value) {
        SearchBar(
            searchQueryText,
            onQueryChange = { onQueryChange.invoke(it) },
            onDismiss = { enableSearchbar.value = false })
    } else {
        Column(modifier = modifier) {

            TopAppBar(
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        style = fontBold14(MaterialTheme.colors.secondary)
                    )
                },
                navigationIcon = if (enableNavigation) {
                    {
                        IconButton(onClick = { navigationOnClick.invoke() }) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowRight,
                                contentDescription = "navigation",
                                tint = navigateIconColor
                            )
                        }
                    }
                } else null,
                actions = {
                    actions.forEach { appbarItem ->
                        IconButton(onClick = { actionOnClick.invoke(appbarItem.id) }) {
                            Icon(
                                imageVector = appbarItem.imageVector,
                                tint = appbarItem.iconColor,
                                contentDescription = "action appbar"
                            )
                        }
                    }

                    if (popupItems.isNotEmpty()) {
                        Popup(popupItems = popupItems) {
                            popupItemOnClick.invoke(it)
                        }
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
//                colors = TopAppBarDefaults.topAppBarColors(
////                        MaterialTheme.colorScheme.background
//                ),
//                scrollBehavior = scrollBehavior,
//                windowInsets = TopAppBarDefaults.windowInsets
            )

//            if (scrollBehavior?.state?.contentOffset.orZero() < -25f) {
//                Divider(Modifier.height(1.dp), color = MaterialTheme.colorScheme.surfaceVariant)
//            }

        }

    }


}

@Composable
fun Popup(modifier: Modifier = Modifier, popupItems: List<PopupItem>, onClick: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp)),
            onClick = { expanded = true }
        ) {
            Icon(
//                modifier = Modifier.size(16.dp),
                imageVector = Icons.Rounded.MoreVert,
                tint = MaterialTheme.colors.secondary,
                contentDescription = "action appbar"
            )
        }




        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(RoundedCornerShape(16.dp)),
            colors = MaterialTheme.colors
        ) {

            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colors.onBackground),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                popupItems.forEach {
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onClick.invoke(it.id)
                        }
                    ) {
                        Row {
                            Icon(
                                painter = it.icon,
                                contentDescription = it.title,
                                tint = MaterialTheme.colors.secondary
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = it.title,
                                style = fontRegular14(MaterialTheme.colors.secondary)
                            )

                        }
                    }
                }
            }

        }
    }
}

@Composable
fun SearchBar(
    searchQueryText: MutableState<String> = remember { mutableStateOf("") },
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(searchQueryText.value) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current



    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = text,
        onValueChange = {
            text = it
            onQueryChange.invoke(it)
        },
        singleLine = true,
        textStyle = fontRegular14(SECONDARY500),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        colors = TextFieldDefaults.textFieldColors(
//            cursorColor = Primary500,
//            focusedLeadingIconColor = Secondary700,
//            containerColor = Color.White,
//            focusedTextColor = Secondary800,
//            unfocusedTextColor = Secondary500,
//            focusedIndicatorColor = Primary500,
//            unfocusedIndicatorColor = Secondary500
        ),
        trailingIcon = {
            IconButton(onClick = {
                onQueryChange("")
                onDismiss()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "leading",
                    tint = SECONDARY500
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}