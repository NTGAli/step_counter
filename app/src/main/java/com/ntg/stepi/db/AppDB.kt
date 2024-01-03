package com.ntg.stepi.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ntg.stepi.db.dao.SocialDao
import com.ntg.stepi.db.dao.StepDao
import com.ntg.stepi.models.Social
import com.ntg.stepi.models.Step

@Database(entities = [Step::class, Social::class], version = 16)
abstract class AppDB: RoomDatabase()  {

    abstract fun stepDao(): StepDao
    abstract fun socialDao(): SocialDao
}