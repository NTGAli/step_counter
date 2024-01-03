package com.ntg.stepi.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ntg.stepi.models.Social

@Dao
interface SocialDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(social: Social)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(social: List<Social>)

    @Update
    suspend fun update(social: Social)

    @Delete
    suspend fun delete(social: Social)

    @Query("SELECT * FROM Social")
    fun getSocials(): LiveData<List<Social>>

    @Query("DELETE FROM Social")
    suspend fun clearAll()

    @Query("SELECT * FROM Social WHERE id =:id")
    fun getSocial(id: Int): LiveData<Social>
}