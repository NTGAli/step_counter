package com.ntg.stepcounter.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log


class NetworkChangeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, p1: Intent?) {
        try {
            if (isOnline(context)) {
                StepCounterService().checkForSync()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            netInfo != null && netInfo.isConnected
        } catch (e: java.lang.NullPointerException) {
            e.printStackTrace()
            false
        }
    }
}