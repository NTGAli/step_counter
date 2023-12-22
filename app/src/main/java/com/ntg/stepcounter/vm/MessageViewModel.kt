package com.ntg.stepcounter.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.stepcounter.api.ApiService
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.models.ResponseBody
import com.ntg.stepcounter.models.UserStore
import com.ntg.stepcounter.models.res.MessageRes
import com.ntg.stepcounter.util.extension.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userStore: UserStore
): ViewModel() {

    private var messages: MutableLiveData<NetworkResult<ResponseBody<List<MessageRes>?>>> = MutableLiveData()
    private var messagesResult: MutableLiveData<NetworkResult<ResponseBody<String?>>> = MutableLiveData()


    fun getMessages() = userStore.messagesId

    fun setMessageId(messageIds: String) = viewModelScope.launch {
        userStore.saveMessageIds(messageIds)
    }

    fun messages(uid: String): MutableLiveData<NetworkResult<ResponseBody<List<MessageRes>?>>> {
        viewModelScope.launch {
                messages = safeApiCall(Dispatchers.IO){
                    apiService.messages(uid)
                } as MutableLiveData<NetworkResult<ResponseBody<List<MessageRes>?>>>
        }
        return messages
    }

    fun readMessage(list: String, uid: String): MutableLiveData<NetworkResult<ResponseBody<String?>>> {
        viewModelScope.launch {
            messagesResult = safeApiCall(Dispatchers.IO){
                apiService.readMessages(list, uid)
            } as MutableLiveData<NetworkResult<ResponseBody<String?>>>
        }
        return messagesResult
    }

}