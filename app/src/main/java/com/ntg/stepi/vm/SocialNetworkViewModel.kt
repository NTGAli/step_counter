package com.ntg.stepi.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.stepi.api.ApiService
import com.ntg.stepi.api.NetworkResult
import com.ntg.stepi.db.AppDB
import com.ntg.stepi.models.ResponseBody
import com.ntg.stepi.models.Social
import com.ntg.stepi.models.SocialNetwork
import com.ntg.stepi.util.extension.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialNetworkViewModel @Inject constructor(
    private val appDB: AppDB,
    private val apiService: ApiService
): ViewModel() {

    private var socialResult: MutableLiveData<NetworkResult<ResponseBody<Int?>>> = MutableLiveData()
    private var deleteResult: MutableLiveData<NetworkResult<ResponseBody<Int?>>> = MutableLiveData()

    val socialNetworks = listOf(
        SocialNetwork(
            name = "اینستاگرام",
            link = "https://instagram.com/PAGE_NAME"
        ),
        SocialNetwork(
            name = "تلگرام",
            link = "https://t.me/PAGE_NAME"
        ),
        SocialNetwork(
            name = "ایکس",
            link = "https://twitter.com/PAGE_NAME"
        ),
        SocialNetwork(
            name = "ایتا",
            link = "https://eitaa.com/PAGE_NAME"
        )
    )


    fun insertNew(social: Social) = viewModelScope.launch { appDB.socialDao().insert(social) }
    fun insertAll(social: List<Social>) = viewModelScope.launch { appDB.socialDao().insertAll(social) }
    fun update(social: Social) = viewModelScope.launch { appDB.socialDao().update(social) }
    fun delete(social: Social) = viewModelScope.launch { appDB.socialDao().delete(social) }
    fun getAll() = appDB.socialDao().getSocials()
    fun getSocial(id: Int) = appDB.socialDao().getSocial(id)



    fun clearAllSocials() = viewModelScope.launch { appDB.socialDao().clearAll() }

    fun insertInServer(uid: String, social: Social): MutableLiveData<NetworkResult<ResponseBody<Int?>>> {
        viewModelScope.launch {
            socialResult = safeApiCall(Dispatchers.IO){
                apiService.insertSocial(uid,social.name, social.pageId)
            } as MutableLiveData<NetworkResult<ResponseBody<Int?>>>
        }
        return socialResult
    }


    fun updateInServer(uid: String, social: Social): MutableLiveData<NetworkResult<ResponseBody<Int?>>> {
        viewModelScope.launch {
            socialResult = safeApiCall(Dispatchers.IO){
                apiService.updateSocial(uid,social.id,social.name, social.pageId)
            } as MutableLiveData<NetworkResult<ResponseBody<Int?>>>
        }
        return socialResult
    }


    fun deleteInServer(uid: String, id: Int): MutableLiveData<NetworkResult<ResponseBody<Int?>>> {
        viewModelScope.launch {
            deleteResult = safeApiCall(Dispatchers.IO){
                apiService.deleteSocial(uid, id)
            } as MutableLiveData<NetworkResult<ResponseBody<Int?>>>
        }
        return deleteResult
    }
}