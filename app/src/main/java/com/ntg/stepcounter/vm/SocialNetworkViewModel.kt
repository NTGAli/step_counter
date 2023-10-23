package com.ntg.stepcounter.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.stepcounter.db.AppDB
import com.ntg.stepcounter.models.Social
import com.ntg.stepcounter.models.SocialNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialNetworkViewModel @Inject constructor(
    private val appDB: AppDB
): ViewModel() {

    val socialNetworks = listOf(
        SocialNetwork(
            name = "اینستاگرام",
            link = "https://instagram.com/PAGE_NAME"
        ),
        SocialNetwork(
            name = "تلگرام",
            link = "https://telegram.me/PAGE_NAME"
        ),
        SocialNetwork(
            name = "ایکس",
            link = "https://telegram.me/PAGE_NAME"
        ),
        SocialNetwork(
            name = "بله",
            link = "https://web.bale.ai/PAGE_NAME"
        ),
        SocialNetwork(
            name = "ایتا",
            link = "https://web.eitaa.com/PAGE_NAME"
        )
    )


    fun insertNew(social: Social) = viewModelScope.launch { appDB.socialDao().insert(social) }
    fun insertAll(social: List<Social>) = viewModelScope.launch { appDB.socialDao().insertAll(social) }
    fun update(social: Social) = viewModelScope.launch { appDB.socialDao().update(social) }
    fun delete(social: Social) = viewModelScope.launch { appDB.socialDao().delete(social) }
    fun getAll() = appDB.socialDao().getSocials()
    fun getSocial(id: Int) = appDB.socialDao().getSocial(id)


}