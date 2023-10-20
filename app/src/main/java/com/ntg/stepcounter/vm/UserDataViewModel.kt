package com.ntg.stepcounter.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.stepcounter.api.ApiService
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.models.ResponseBody
import com.ntg.stepcounter.models.UserStore
import com.ntg.stepcounter.models.res.FosDetailsRes
import com.ntg.stepcounter.models.res.StepRes
import com.ntg.stepcounter.models.res.UserProfile
import com.ntg.stepcounter.models.res.UserRes
import com.ntg.stepcounter.util.extension.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val userStore: UserStore,
    private val apiService: ApiService
): ViewModel() {

    private var userProfile: MutableLiveData<NetworkResult<ResponseBody<UserProfile?>>> = MutableLiveData()
    private var fodDetails: MutableLiveData<NetworkResult<ResponseBody<FosDetailsRes?>>> = MutableLiveData()
    private var usersFos: MutableLiveData<NetworkResult<ResponseBody<List<UserRes>?>>> = MutableLiveData()
    private var userSteps: MutableLiveData<NetworkResult<ResponseBody<List<StepRes>?>>> = MutableLiveData()
    private var clapResult: MutableLiveData<NetworkResult<ResponseBody<Any?>>> = MutableLiveData()


    fun getUsername() = userStore.getUserName
    fun getPhoneNumber() = userStore.getPhoneNumber
    fun getUserId() = userStore.getUserID
    fun getGradeId() = userStore.getGradeId
    fun getUserStatus() = userStore.getStatus
    fun getFieldStudy() = userStore.fieldStudy
    fun getFosId() = userStore.getFosId
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

    fun setGradeId(id: Int) = viewModelScope.launch {
        userStore.setGradeId(id)
    }

    fun setFosId(id: Int) = viewModelScope.launch {
        userStore.setFosId(id)
    }

    fun getUserProfile(uid: String, userId: String): MutableLiveData<NetworkResult<ResponseBody<UserProfile?>>> {
            viewModelScope.launch {
                userProfile = safeApiCall(Dispatchers.IO){
                    apiService.userProfile(uid, userId)
                } as MutableLiveData<NetworkResult<ResponseBody<UserProfile?>>>
            }
        return userProfile
    }


    fun getFosDetails(fosId: String): MutableLiveData<NetworkResult<ResponseBody<FosDetailsRes?>>> {
        viewModelScope.launch {
            fodDetails = safeApiCall(Dispatchers.IO){
                apiService.fosDetails(fosId)
            } as MutableLiveData<NetworkResult<ResponseBody<FosDetailsRes?>>>
        }
        return fodDetails
    }

    fun userOfFos(fosId: String): MutableLiveData<NetworkResult<ResponseBody<List<UserRes>?>>> {
        viewModelScope.launch {
            usersFos = safeApiCall(Dispatchers.IO){
                apiService.userOfFos(fosId)
            } as MutableLiveData<NetworkResult<ResponseBody<List<UserRes>?>>>
        }
        return usersFos
    }


    fun getUserSteps(uid: String): MutableLiveData<NetworkResult<ResponseBody<List<StepRes>?>>> {
        viewModelScope.launch {
            userSteps = safeApiCall(Dispatchers.IO){
                apiService.userSteps(uid)
            } as MutableLiveData<NetworkResult<ResponseBody<List<StepRes>?>>>
        }
        return userSteps
    }


    fun clap(uid: String, forUid: String): MutableLiveData<NetworkResult<ResponseBody<Any?>>> {
        viewModelScope.launch {
            apiService.clap(uid, forUid)
        }
        return clapResult
    }


}