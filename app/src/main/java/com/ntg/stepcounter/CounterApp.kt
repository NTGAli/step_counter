package com.ntg.stepcounter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CounterApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}