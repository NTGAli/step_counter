package com.ntg.stepcounter.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.ntg.stepcounter.R
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.MessageItem
import com.ntg.stepcounter.models.res.MessageRes
import com.ntg.stepcounter.util.extension.openInBrowser
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.vm.MessageViewModel

@Composable
fun MessagesBoxScreen(
    navHostController: NavHostController,
    messageViewModel: MessageViewModel,
    uid: String
){

    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.messages_box),
                enableNavigation = true,
                navigationOnClick = {
                    navHostController.popBackStack()
                }
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navHostController, messageViewModel, uid)
        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navHostController: NavHostController,
    messageViewModel: MessageViewModel,
    uid: String
){

    val owner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var loadData by remember {
        mutableStateOf(false)
    }
    
    val data = remember {
        mutableStateOf(listOf<MessageRes>())
    }


    LaunchedEffect(key1 = loadData, block = {

        if (data.value.isEmpty()){
            messageViewModel.messages(uid).observe(owner){
                when(it){
                    is NetworkResult.Error -> {
                        timber("Messages ERROR ${it.message}")
                        loadData = false
                    }
                    is NetworkResult.Loading -> {
                        timber("Messages ld")
                        loadData = true
                    }
                    is NetworkResult.Success -> {
                        timber("Messages sc ${it.data?.data}")
                        loadData = false
                        data.value = it.data?.data.orEmpty()
                    }
                }
            }
        }

    })


    var messages by remember {
        mutableStateOf(listOf(""))
    }


    messageViewModel.getMessages().collectAsState(initial = "").value.let {
        try {
            messages = Gson().fromJson(it, Array<String>::class.java).asList()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    LazyColumn(content = {
        
        items(data.value.sortedByDescending { it.id }){message ->
            
            MessageItem(
                modifier = Modifier.padding(horizontal = 24.dp).padding(top = 16.dp),
                id = message.id,
                title = message.title,
                text = message.description,
                date = message.date,
                isRead = messages.contains(message.id),
                buttonText = message.buttonText,
                parentOnClick = {
                    if (message.buttonText.orEmpty().isEmpty()){
                        readMessage(it, messages.toMutableList(),uid, messageViewModel,owner)

                    }
                },
                onClickButton = {
                    readMessage(it, messages.toMutableList(),uid, messageViewModel,owner)
                    context.openInBrowser(message.link.orEmpty())

                }
            )
            
        }
        
    })

}

private fun readMessage(id: String,messages: List<String>,uid: String, messageViewModel: MessageViewModel, owner: LifecycleOwner){
    val readId = messages.toMutableList()
    readId.add(id)
    messageViewModel.setMessageId(Gson().toJson(readId))
    messageViewModel.readMessage(Gson().toJson(readId.distinct().filter { it.isNotBlank() }), uid).observe(owner){
        when(it){
            is NetworkResult.Error -> {

            }
            is NetworkResult.Loading -> {

            }
            is NetworkResult.Success -> {

            }
        }
    }

}