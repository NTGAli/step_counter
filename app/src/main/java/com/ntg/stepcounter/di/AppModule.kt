package com.ntg.stepcounter.di

import android.content.Context
import androidx.room.Room
import com.ntg.stepcounter.db.AppDB
import com.ntg.stepcounter.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAppDB(@ApplicationContext context: Context): AppDB {

        return Room.databaseBuilder(
            context = context,
            AppDB::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    }
}