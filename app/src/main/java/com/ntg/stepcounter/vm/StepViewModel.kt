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
import com.ntg.stepcounter.models.res.StepSynced
import com.ntg.stepcounter.models.res.SummariesRes
import com.ntg.stepcounter.util.extension.dateOfToday
import com.ntg.stepcounter.util.extension.safeApiCall
import com.ntg.stepcounter.util.extension.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private var syncResult: MutableLiveData<NetworkResult<ResponseBody<StepSynced?>>> = MutableLiveData()
    private var summaries: MutableLiveData<NetworkResult<ResponseBody<SummariesRes?>>> = MutableLiveData()

    fun clearSummaries(){
        summaries = MutableLiveData()
    }

    fun insertStep() = viewModelScope.launch {

        val dateOfToday = dateOfToday()
        val rowsUpdated = appDB.stepDao().updateCount(dateOfToday)

        if (rowsUpdated == 0) {
            // If no rows were updated, insert a new row with count = 1
            val newEntity = Step(0,date = dateOfToday, count =  1) // Replace with your entity constructor
            appDB.stepDao().insert(newEntity)
        }
    }

    fun insertStep(count: Int, updateId: Int) = viewModelScope.launch {

        timber("ajkhdjakdkjawhdjkwhakjdh $updateId")
        val dateOfToday = dateOfToday()

        if (updateId != -1){
            val rowsUpdated = appDB.stepDao().updateCount(updateId, count)
            if (rowsUpdated == 0) {
                val newEntity = Step(0,date = dateOfToday, start  =  count)
                appDB.stepDao().insert(newEntity)
            }
        }else{
            val newEntity = Step(0,date = dateOfToday, start  =  count)
            appDB.stepDao().insert(newEntity)
        }

//        val rowsUpdated = appDB.stepDao().updateCount(dateOfToday, count)

    }

    fun insertManually(step: Step){
        viewModelScope.launch {
            appDB.stepDao().insert(step)
        }
    }

    fun updateSync(date: String, sync: Int) = viewModelScope.launch { appDB.stepDao().updateSync(date, sync) }

    fun getAllSteps() = appDB.stepDao().getAllSteps()

    fun getAllDate() = appDB.stepDao().getAllDate()

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

    fun syncStep(uid: String, step: Step): MutableLiveData<NetworkResult<ResponseBody<StepSynced?>>> {
        viewModelScope.launch {
            syncResult = safeApiCall(Dispatchers.IO){
                apiService.syncSteps(uid, step.date, step.count)
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

    fun summariesData(uid: String, fetch: Boolean): MutableLiveData<NetworkResult<ResponseBody<SummariesRes?>>> {
        if (fetch || summaries.value == null){
            viewModelScope.launch {
                summaries = safeApiCall(Dispatchers.IO){
                    apiService.summariesData(uid)
                } as MutableLiveData<NetworkResult<ResponseBody<SummariesRes?>>>
            }
        }
        return summaries
    }

}
