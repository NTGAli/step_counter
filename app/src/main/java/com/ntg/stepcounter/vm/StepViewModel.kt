package com.ntg.stepcounter.vm

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.stepcounter.api.ApiService
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.db.AppDB
import com.ntg.stepcounter.models.ResponseBody
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.models.TopRecord
import com.ntg.stepcounter.models.res.DataChallenge
import com.ntg.stepcounter.models.res.StepSynced
import com.ntg.stepcounter.models.res.SummariesRes
import com.ntg.stepcounter.models.res.SummaryRes
import com.ntg.stepcounter.models.res.UserWinnerData
import com.ntg.stepcounter.util.extension.dateOfToday
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.safeApiCall
import com.ntg.stepcounter.util.extension.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class StepViewModel @Inject constructor(
    private val appDB: AppDB,
    private val apiService: ApiService
) : ViewModel() {

    private var stepsOfToday: LiveData<List<Step>> = MutableLiveData()
    private var topRecord: LiveData<TopRecord?>? = MutableLiveData()
    private var unSynced: LiveData<List<Step?>> = MutableLiveData()
    private var syncResult: MutableLiveData<NetworkResult<ResponseBody<StepSynced?>>> =
        MutableLiveData()
    private var summaries: MutableLiveData<NetworkResult<ResponseBody<SummariesRes?>>> =
        MutableLiveData()
    private var dataChallenge: MutableLiveData<NetworkResult<ResponseBody<DataChallenge?>>> =
        MutableLiveData()
    private var winners: MutableLiveData<NetworkResult<ResponseBody<List<UserWinnerData>?>>> =
        MutableLiveData()
    private var usersBase: MutableLiveData<NetworkResult<ResponseBody<List<SummaryRes>?>>> =
        MutableLiveData()

    fun clearSummaries() {
        summaries = MutableLiveData()
    }

    fun clearUserSteps() = viewModelScope.launch { appDB.stepDao().clearUserSteps() }

    fun insertStep() = viewModelScope.launch {

        val dateOfToday = dateOfToday()
        val rowsUpdated = appDB.stepDao().updateCount(dateOfToday)

        if (rowsUpdated == 0) {
            // If no rows were updated, insert a new row with count = 1
            val newEntity =
                Step(0, date = dateOfToday, count = 1) // Replace with your entity constructor
            appDB.stepDao().insert(newEntity)
        }
    }

    fun updateSync(date: String, count: Int) =
        viewModelScope.launch { appDB.stepDao().updateSync(date, count) }

    fun getAllSteps() = appDB.stepDao().getAllSteps()

    fun getAllDate() = appDB.stepDao().getAllDate()

    fun insertAll(steps: List<Step>) = viewModelScope.launch { appDB.stepDao().insertAll(steps) }

    fun numberOfDate() = appDB.stepDao().numberOfDate()

    fun getToday(): LiveData<List<Step>> {
        val dateOfToday = dateOfToday()

        viewModelScope.launch {
            stepsOfToday = appDB.stepDao().getToday(dateOfToday)
        }
        return stepsOfToday
    }

    fun topRecord(): LiveData<TopRecord?>? {
        viewModelScope.launch {
            topRecord = appDB.stepDao().topRecord()
        }
        return topRecord
    }

    fun syncStep(
        date: String,
        steps: Int,
        uid: String
    ): MutableLiveData<NetworkResult<ResponseBody<StepSynced?>>> {
        viewModelScope.launch {
            syncResult = safeApiCall(Dispatchers.IO) {
                timber("StepSync -----")
                apiService.syncSteps(
                    date, steps, uid
                )
            } as MutableLiveData<NetworkResult<ResponseBody<StepSynced?>>>
        }
        return syncResult
    }

    fun needToSyncSteps(): LiveData<List<Step?>> {
        viewModelScope.launch {
            unSynced = appDB.stepDao().getUnSyncedSteps()
        }
        return unSynced
    }

    fun summariesData(
        uid: String,
        fetch: Boolean
    ): MutableLiveData<NetworkResult<ResponseBody<SummariesRes?>>> {
        timber("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj")
        if (fetch || summaries.value == null) {
            viewModelScope.launch {
                summaries = safeApiCall(Dispatchers.IO) {
                    apiService.summariesData(uid)
                } as MutableLiveData<NetworkResult<ResponseBody<SummariesRes?>>>
            }
        }
        return summaries
    }


    fun gerUserBase(base: String): MutableLiveData<NetworkResult<ResponseBody<List<SummaryRes>?>>> {
        viewModelScope.launch {
            usersBase = safeApiCall(Dispatchers.IO) {
                apiService.getUserBase(base)
            } as MutableLiveData<NetworkResult<ResponseBody<List<SummaryRes>?>>>
        }
        return usersBase
    }

    fun dataChallenge(
        uid: String,
    ): MutableLiveData<NetworkResult<ResponseBody<DataChallenge?>>> {
            viewModelScope.launch {
                dataChallenge = safeApiCall(Dispatchers.IO) {
                    apiService.dataChallenge(uid)
                } as MutableLiveData<NetworkResult<ResponseBody<DataChallenge?>>>
            }
        return dataChallenge
    }


    fun winners(): MutableLiveData<NetworkResult<ResponseBody<List<UserWinnerData>?>>> {
        viewModelScope.launch {
            winners = safeApiCall(Dispatchers.IO) {
                apiService.winners()
            } as MutableLiveData<NetworkResult<ResponseBody<List<UserWinnerData>?>>>
        }
        return winners
    }

}
