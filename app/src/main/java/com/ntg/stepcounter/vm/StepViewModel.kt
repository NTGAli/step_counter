package com.ntg.stepcounter.vm

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.stepcounter.db.AppDB
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.models.TopRecord
import com.ntg.stepcounter.util.extension.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class StepViewModel @Inject constructor(
    private val appDB: AppDB
) : ViewModel() {

    private var stepsOfToday: LiveData<List<Step>> = MutableLiveData()
    private var topRecord: LiveData<TopRecord> = MutableLiveData()

    fun insertStep() = viewModelScope.launch {


        val dateOfToday = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().toString()
        } else {
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateFormat.format(currentDate)
        }
        appDB.stepDao().insert(
            Step(
                0,
                date = dateOfToday, timeUnix = System.currentTimeMillis().toString()
            )
        )
    }

    fun insertManually(step: Step){
        viewModelScope.launch {
            appDB.stepDao().insert(step)
        }
    }

    fun getAll() = appDB.stepDao().getAll()

    fun numberOfDate() = appDB.stepDao().numberOfDate()

    fun getToday(): LiveData<List<Step>> {
        val dateOfToday = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().toString()
        } else {
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateFormat.format(currentDate)
        }

        viewModelScope.launch {
            stepsOfToday = appDB.stepDao().getToday(dateOfToday)
        }
        return stepsOfToday
    }

    fun topRecord(): LiveData<TopRecord> {
        viewModelScope.launch {
            topRecord = appDB.stepDao().topRecord()
        }
        return topRecord
    }

}