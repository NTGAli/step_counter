package com.ntg.stepcounter.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.stepcounter.models.UserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val userStore: UserStore
): ViewModel() {


    fun getUsername() = userStore.getUserName
    fun getPhoneNumber() = userStore.getPhoneNumber
    fun getUserId() = userStore.getUserID
    fun getUserStatus() = userStore.getStatus
    fun getFieldStudy() = userStore.fieldStudy
    fun isVerified() = userStore.isVerified
    fun isShowReport() = userStore.showReport
    fun isAutoDetect() = userStore.isAutoDetect

    fun setUsername(username: String) = viewModelScope.launch {
        userStore.saveUsername(username)
    }

    fun setUserStatus(status: String) = viewModelScope.launch {
        userStore.saveSTATUS(status)
    }

    fun setFieldStudy(fieldStudy: String) = viewModelScope.launch {
        userStore.saveFiledStudy(fieldStudy)
    }

    fun setUserId(userId: String) = viewModelScope.launch {
        userStore.saveUserID(userId)
    }

    fun setPhone(phone: String) = viewModelScope.launch {
        userStore.savePhoneNumber(phone)
    }

    fun isShowReport(showReport: Boolean) = viewModelScope.launch {
        userStore.isShowReport(showReport)
    }

    fun isAutoDetect(isAuto: Boolean) = viewModelScope.launch {
        userStore.isAutoDetect(isAuto)
    }

}