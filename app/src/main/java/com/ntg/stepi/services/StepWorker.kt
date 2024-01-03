package com.ntg.stepi.services

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ntg.stepi.util.extension.foregroundServiceRunning


class StepWorker(private val context: Context, private val params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        if (!context.foregroundServiceRunning()) {
//            Log.d(TAG, "starting service from doWork")
            val intent = Intent(context, StepCounterService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
        return Result.success()
    }
}