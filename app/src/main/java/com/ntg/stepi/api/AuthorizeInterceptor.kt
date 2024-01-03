package com.ntg.stepi.api

import com.ntg.stepi.BuildConfig.API_TOKEN
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizeInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $API_TOKEN")
            .build()
        return chain.proceed(request)
    }

}