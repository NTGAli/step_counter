package com.ntg.stepcounter.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ntg.stepcounter.models.Step

@Dao
interface StepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(step: Step)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(step: List<Step>)

    @Update
    suspend fun update(step: Step)

    @Query("SELECT * FROM Step")
    fun getAll(): LiveData<List<Step>>


}