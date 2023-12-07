package com.ntg.stepcounter.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ntg.stepcounter.StepCounterService

class StartupAfterReboot: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED){
            val serviceIntent = Intent(p0, StepCounterService::class.java)
            p0?.startService(serviceIntent)
        }
    }
}