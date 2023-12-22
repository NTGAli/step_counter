package com.ntg.stepcounter.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ntg.stepcounter.api.ApiService
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.models.ResponseBody
import com.ntg.stepcounter.models.UserStore
import com.ntg.stepcounter.models.res.AccountStateRes
import com.ntg.stepcounter.models.res.Achievement
import com.ntg.stepcounter.models.res.FosDetailsRes
import com.ntg.stepcounter.models.res.StepRes
import com.ntg.stepcounter.models.res.UpdateRes
import com.ntg.stepcounter.models.res.UserProfile
import com.ntg.stepcounter.models.res.UserRes
import com.ntg.stepcounter.util.extension.safeApiCall
import com.ntg.stepcounter.util.extension.timber
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
    private var updateInfo: MutableLiveData<NetworkResult<ResponseBody<UpdateRes?>>> = MutableLiveData()
    private var fodDetails: MutableLiveData<NetworkResult<ResponseBody<FosDetailsRes?>>> = MutableLiveData()
    private var signInData: MutableLiveData<NetworkResult<ResponseBody<UserProfile?>>> = MutableLiveData()
    private var usersFos: MutableLiveData<NetworkResult<ResponseBody<List<UserRes>?>>> = MutableLiveData()
    private var userSteps: MutableLiveData<NetworkResult<ResponseBody<List<StepRes>?>>> = MutableLiveData()
    private var clapsData: MutableLiveData<NetworkResult<ResponseBody<List<UserRes>?>>> = MutableLiveData()
    private var userDataAchievement: MutableLiveData<NetworkResult<ResponseBody<Achievement?>>> = MutableLiveData()
    private var lockResult: MutableLiveData<NetworkResult<ResponseBody<Boolean?>>> = MutableLiveData()
    private var fcmResult: MutableLiveData<NetworkResult<ResponseBody<String?>>> = MutableLiveData()
    private var accountStateDate: MutableLiveData<NetworkResult<ResponseBody<AccountStateRes?>>> = MutableLiveData()
    private var clapResult: MutableLiveData<NetworkResult<ResponseBody<Any?>>> = MutableLiveData()


    fun getUsername() = userStore.getUserName
    fun getFCM() = userStore.getFCMToken
    fun getAchievement() = userStore.getAchievement
    fun getTimeSign() = userStore.getTimeSign
    fun getPhoneNumber() = userStore.getPhoneNumber
    fun getUserId() = userStore.getUserID
    fun getTheme() = userStore.getTheme
    fun getGradeId() = userStore.getGradeId
    fun getUserStatus() = userStore.getStatus
    fun getFieldStudy() = userStore.fieldStudy
    fun getMessagesId() = userStore.messagesId
    fun getFosId() = userStore.getFosId
    fun getClaps() = userStore.getClaps
    fun isVerified() = userStore.isVerified
    fun isBlocked() = userStore.isBlocked
    fun isShowReport() = userStore.showReport
    fun isAutoDetect() = userStore.isAutoDetect
    fun deadCode() = userStore.getDeadCode

    fun clearUserData() = viewModelScope.launch { userStore.clearUserData() }

    fun setUsername(username: String) = viewModelScope.launch {
        userStore.saveUsername(username)
    }

    fun setFCM(fcm: String) = viewModelScope.launch {
        userStore.saveFCMToken(fcm)
    }

    fun satMessagesId(ids: String) = viewModelScope.launch {
        userStore.saveMessageIds(ids)
    }

    fun setTheme(theme: String) = viewModelScope.launch {
        userStore.saveTheme(theme)
    }

    fun setTimeSign(timeSing: String) = viewModelScope.launch {
        userStore.saveTimeSign(timeSing)
    }

    fun setUserStatus(status: String) = viewModelScope.launch {
        userStore.saveSTATUS(status)
    }

    fun setDeadCode(deadCode: Int) = viewModelScope.launch {
        userStore.saveDeadCode(deadCode)
    }

    fun setFieldStudy(fieldStudy: String) = viewModelScope.launch {
        userStore.saveFiledStudy(fieldStudy)
    }

    fun setUserId(userId: String) = viewModelScope.launch {
        userStore.saveUserID(userId)
    }

    fun setAchievement(achievement: String) = viewModelScope.launch {
        userStore.saveAchievement(achievement)
    }

    fun isVerified(isVerified: Boolean) = viewModelScope.launch {
        userStore.isVerified(isVerified)
    }

    fun isBlocked(isBlocked: Boolean) = viewModelScope.launch {
        userStore.isBlocked(isBlocked)
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

    fun setClaps(claps: Int) = viewModelScope.launch {
        userStore.setClaps(claps)
    }

    fun getUserProfile(uid: String, userId: String): MutableLiveData<NetworkResult<ResponseBody<UserProfile?>>> {
            viewModelScope.launch {
                userProfile = safeApiCall(Dispatchers.IO){
                    apiService.userProfile(uid, userId)
                } as MutableLiveData<NetworkResult<ResponseBody<UserProfile?>>>
            }
        return userProfile
    }


    fun updateInfo(): MutableLiveData<NetworkResult<ResponseBody<UpdateRes?>>> {
        viewModelScope.launch {
            updateInfo = safeApiCall(Dispatchers.IO){
                apiService.updateInfo()
            } as MutableLiveData<NetworkResult<ResponseBody<UpdateRes?>>>
        }
        return updateInfo
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


    fun accountState(uid: String): MutableLiveData<NetworkResult<ResponseBody<AccountStateRes?>>> {
        viewModelScope.launch {
            if (accountStateDate.value == null){
                accountStateDate = safeApiCall(Dispatchers.IO){
                    apiService.accountSate(uid)
                } as MutableLiveData<NetworkResult<ResponseBody<AccountStateRes?>>>
            }
        }
        return accountStateDate
    }


    fun clap(uid: String, forUid: String): MutableLiveData<NetworkResult<ResponseBody<Any?>>> {
        viewModelScope.launch {
            apiService.clap(uid, forUid)
        }
        return clapResult
    }

    fun clapsData(uid: String): MutableLiveData<NetworkResult<ResponseBody<List<UserRes>?>>> {
//        if (clapsData.value == null){

            viewModelScope.launch {
                clapsData = safeApiCall(Dispatchers.IO){
                    apiService.clapData(uid)
                } as MutableLiveData<NetworkResult<ResponseBody<List<UserRes>?>>>
            }
//        }
        return clapsData
    }

    fun signIn(uid: String, phone: String, timeSign: String): MutableLiveData<NetworkResult<ResponseBody<UserProfile?>>> {
        viewModelScope.launch {
            signInData = safeApiCall(Dispatchers.IO){
                apiService.signIn(uid, phone, timeSign)
            } as MutableLiveData<NetworkResult<ResponseBody<UserProfile?>>>
        }
        return signInData
    }

    fun userAchievement(uid: String): MutableLiveData<NetworkResult<ResponseBody<Achievement?>>> {
        if (userDataAchievement.value == null){

            viewModelScope.launch {
                userDataAchievement = safeApiCall(Dispatchers.IO){
                    apiService.userAchievement(uid)
                } as MutableLiveData<NetworkResult<ResponseBody<Achievement?>>>
            }
        }
        return userDataAchievement
    }


    fun setLock(uid: String, isLock: Boolean): MutableLiveData<NetworkResult<ResponseBody<Boolean?>>> {
            viewModelScope.launch {
                lockResult = safeApiCall(Dispatchers.IO){
                    apiService.setLock(uid = uid, isLock = isLock)
                } as MutableLiveData<NetworkResult<ResponseBody<Boolean?>>>
            }
        return lockResult
    }


    fun syncFCM(uid: String, fcm: String): MutableLiveData<NetworkResult<ResponseBody<String?>>> {
        viewModelScope.launch {
            fcmResult = safeApiCall(Dispatchers.IO){
                apiService.syncFCM(uid = uid, fcm = fcm)
            } as MutableLiveData<NetworkResult<ResponseBody<String?>>>
        }
        return fcmResult
    }


}