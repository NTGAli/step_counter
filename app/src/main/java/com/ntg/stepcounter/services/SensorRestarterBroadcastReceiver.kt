package com.ntg.stepcounter.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ntg.stepcounter.util.extension.foregroundServiceRunning
import com.ntg.stepcounter.util.extension.timber


class SensorRestarterBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        timber("SensorRestarterBroadcastReceiver ::: start")

        try {
            val workManager = WorkManager.getInstance(context)
            val startServiceRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(StepWorker::class.java)
                .build()
            workManager.enqueue(startServiceRequest)
        }catch (e: Exception){e.printStackTrace()}


        if (!context.foregroundServiceRunning()){
            val serviceIntent = Intent(context, StepCounterService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}