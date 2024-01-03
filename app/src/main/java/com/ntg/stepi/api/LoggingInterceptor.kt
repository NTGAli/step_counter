package com.ntg.stepi.api

import com.ntg.stepi.util.extension.timber
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor {
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor {
            timber("HttpLog: log: http log: $it")
        }.setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}