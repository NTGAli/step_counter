package com.ntg.stepcounter.api

import com.ntg.stepcounter.util.extension.timber
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor {
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor {
            timber("HttpLog: log: http log: $it")
        }.setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}