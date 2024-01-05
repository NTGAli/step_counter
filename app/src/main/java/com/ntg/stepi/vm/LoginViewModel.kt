package com.ntg.stepi.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.stepi.api.ApiService
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.models.FieldOfStudy
import com.ntg.stepi.models.ResponseBody
import com.ntg.stepi.util.extension.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService
): ViewModel() {

    private var loginResult: MutableLiveData<NetworkResult<ResponseBody<String?>>> = MutableLiveData()
    private var registerResult: MutableLiveData<NetworkResult<ResponseBody<String?>>> = MutableLiveData()
    private var editResult: MutableLiveData<NetworkResult<ResponseBody<String?>>> = MutableLiveData()
    private var insertFos: MutableLiveData<NetworkResult<ResponseBody<Int?>>> = MutableLiveData()
    private var editPhoneResult: MutableLiveData<NetworkResult<ResponseBody<String?>>> = MutableLiveData()
    private var listOfFos: MutableLiveData<NetworkResult<ResponseBody<List<FieldOfStudy>?>>> = MutableLiveData()
    private var listOfJobs: MutableLiveData<NetworkResult<ResponseBody<List<FieldOfStudy>?>>> = MutableLiveData()
    var fieldOfStudy: FieldOfStudy? = null
    var job: FieldOfStudy? = null


    fun login(phone: String): MutableLiveData<NetworkResult<ResponseBody<String?>>> {
        viewModelScope.launch {
            loginResult = safeApiCall(Dispatchers.IO){
                apiService.login(phone)
            } as MutableLiveData<NetworkResult<ResponseBody<String?>>>
        }
        return loginResult
    }


    fun register(phone: String, fullName: String, state: String, uid: String, fosId: String, gradeId: String, timeSign: String): MutableLiveData<NetworkResult<ResponseBody<String?>>> {
        viewModelScope.launch {
            registerResult = safeApiCall(Dispatchers.IO){
                apiService.register(fullName, phone, state, uid, fosId, gradeId, timeSign)
            } as MutableLiveData<NetworkResult<ResponseBody<String?>>>
        }
        return registerResult
    }

    fun editUserDate(phone: String, fullName: String, state: String, uid: String, fosId: String, gradeId: String): MutableLiveData<NetworkResult<ResponseBody<String?>>> {
        viewModelScope.launch {
            editResult = safeApiCall(Dispatchers.IO){
                apiService.editUserData(fullName, phone, state, uid, fosId, gradeId)
            } as MutableLiveData<NetworkResult<ResponseBody<String?>>>
        }
        return editResult
    }


    fun editPhoneNumber(phone: String, uid: String): MutableLiveData<NetworkResult<ResponseBody<String?>>> {
        viewModelScope.launch {
            editPhoneResult = safeApiCall(Dispatchers.IO){
                apiService.editPhoneNumber(phone, uid)
            } as MutableLiveData<NetworkResult<ResponseBody<String?>>>
        }
        return editPhoneResult
    }


    fun fieldOfStudies(): MutableLiveData<NetworkResult<ResponseBody<List<FieldOfStudy>?>>> {
        viewModelScope.launch {
            listOfFos = safeApiCall(Dispatchers.IO){
                apiService.filedOfStudies()
            } as MutableLiveData<NetworkResult<ResponseBody<List<FieldOfStudy>?>>>
        }
        return listOfFos
    }


    fun jobs(): MutableLiveData<NetworkResult<ResponseBody<List<FieldOfStudy>?>>> {
        viewModelScope.launch {
            listOfJobs = safeApiCall(Dispatchers.IO){
                apiService.jobList()
            } as MutableLiveData<NetworkResult<ResponseBody<List<FieldOfStudy>?>>>
        }
        return listOfJobs
    }


    fun insertNewFos(title: String): MutableLiveData<NetworkResult<ResponseBody<Int?>>> {
        viewModelScope.launch {
            insertFos = safeApiCall(Dispatchers.IO){
                apiService.insertNewFOS(title)
            } as MutableLiveData<NetworkResult<ResponseBody<Int?>>>
        }
        return insertFos
    }


}