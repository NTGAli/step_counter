package com.ntg.stepi.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ntg.stepi.services.StepCounterService

class StartupAfterReboot: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED){
            val serviceIntent = Intent(context, StepCounterService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(serviceIntent)
            } else {
                context?.startService(serviceIntent)
            }
        }
    }
}