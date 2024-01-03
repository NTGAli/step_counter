package com.ntg.stepi.di

import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ntg.stepi.api.ApiService
import com.ntg.stepi.api.AuthorizeInterceptor
import com.ntg.stepi.api.LoggingInterceptor
import com.ntg.stepi.db.AppDB
import com.ntg.stepi.models.UserStore
import com.ntg.stepi.util.Constants.BASE_URL
import com.ntg.stepi.util.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun provideOkHttp(@ApplicationContext context: Context): OkHttpClient{
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor().httpLoggingInterceptor())
            .addInterceptor(AuthorizeInterceptor())
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService{
        return retrofit.create(ApiService::class.java)
    }

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

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): UserStore {
        return UserStore(context)
    }
}