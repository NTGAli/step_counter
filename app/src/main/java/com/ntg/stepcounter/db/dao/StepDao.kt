package com.ntg.stepcounter.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.models.TopRecord

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

    @Query("SELECT * FROM Step WHERE date =:date")
    fun getToday(date: String): LiveData<List<Step>>

    @Query("SELECT date, COUNT(*) AS record_count\n" +
            "FROM Step \n" +
            "GROUP BY date\n" +
            "ORDER BY record_count DESC\n" +
            "LIMIT 1")
    fun topRecord(): LiveData<TopRecord>

    @Query("SELECT COUNT(DISTINCT date)\n" +
            "FROM step")
    fun numberOfDate(): LiveData<Int>



}