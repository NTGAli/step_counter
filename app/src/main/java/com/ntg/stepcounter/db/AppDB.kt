package com.ntg.stepcounter.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ntg.stepcounter.db.dao.StepDao
import com.ntg.stepcounter.models.Step

@Database(entities = [Step::class], version = 2)
abstract class AppDB: RoomDatabase()  {

    abstract fun stepDao(): StepDao
}