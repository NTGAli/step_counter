package com.ntg.stepcounter.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.models.TopRecord
import com.ntg.stepcounter.util.extension.dateOfToday

@Dao
interface StepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(step: Step): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(step: List<Step>)

    @Update
    suspend fun update(step: Step)

    @Query("DELETE FROM Step")
    suspend fun clearUserSteps()

    @Query("UPDATE Step SET count = count + 1 WHERE date = :date")
    suspend fun updateCount(date: String?): Int

    @Query("UPDATE Step SET count =:count, exp =:exp WHERE id = :id AND count <:count AND start<=:count AND date=:date")
    suspend fun updateCount(id: Int?,date: String, count: Int?, exp: Boolean = false): Int

    @Query("UPDATE Step SET synced =:count WHERE date = :date AND count - start > 0")
    suspend fun updateSync(date: String, count: Int)

    @Query("SELECT SUM(count - start) FROM Step WHERE count != 0")
    fun getAllSteps(): LiveData<Int>

    @Query("SELECT * FROM Step")
    fun getAllDate(): LiveData<List<Step>>

    @Query("SELECT * FROM Step WHERE date =:date")
    fun getToday(date: String): LiveData<List<Step>>

    @Query("SELECT date, SUM(count - start) AS record_count FROM Step WHERE count != 0 GROUP BY date ORDER BY record_count DESC LIMIT 1")
    fun topRecord(): LiveData<TopRecord?>?

    @Query("SELECT COUNT(DISTINCT date)\n" +
            "FROM step")
    fun numberOfDate(): LiveData<Int>

    @Query("SELECT *\n" +
            "FROM Step\n" +
            "WHERE count != synced")
    fun getUnSyncedSteps(): LiveData<List<Step?>>

    @Query("SELECT *\n" +
            "FROM Step\n" +
            "WHERE count != synced AND DATE(date)=:date")
    fun getUnSyncedStepsOfDate(date: String = dateOfToday()): LiveData<List<Step?>>



}