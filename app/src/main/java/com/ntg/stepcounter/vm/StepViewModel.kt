package com.ntg.stepcounter.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.stepcounter.db.AppDB
import com.ntg.stepcounter.models.Step
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StepViewModel @Inject constructor(
    private val appDB: AppDB
): ViewModel() {

    fun insertStep() = viewModelScope.launch { appDB.stepDao().insert(Step(0, System.currentTimeMillis().toString())) }

    fun getAll() = appDB.stepDao().getAll()


}