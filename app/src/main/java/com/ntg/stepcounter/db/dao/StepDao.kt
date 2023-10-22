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

    @Query("UPDATE Step SET count = count + 1 WHERE date = :date")
    suspend fun updateCount(date: String?): Int

    @Query("UPDATE Step SET count =:count WHERE id = :id AND count <:count")
    suspend fun updateCount(id: Int?, count: Int?): Int

    @Query("UPDATE Step SET synced =:sync WHERE date = :date")
    suspend fun updateSync(date: String, sync: Int)

    @Query("SELECT SUM(count) FROM Step")
    fun getAllSteps(): LiveData<Int>

    @Query("SELECT * FROM Step")
    fun getAllDate(): LiveData<List<Step>>

    @Query("SELECT * FROM Step WHERE date =:date")
    fun getToday(date: String): LiveData<List<Step>>

    @Query("SELECT date, MAX(count) AS record_count FROM Step")
    fun topRecord(): LiveData<TopRecord?>?

    @Query("SELECT COUNT(DISTINCT date)\n" +
            "FROM step")
    fun numberOfDate(): LiveData<Int>

    @Query("SELECT *\n" +
            "FROM Step\n" +
            "WHERE count != synced")
    fun getUnSyncedSteps(): LiveData<List<Step?>>



}