package com.ntg.stepcounter.api

import com.ntg.stepcounter.BuildConfig.API_TOKEN
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