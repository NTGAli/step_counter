package com.ntg.stepcounter.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.StatusBarNotification
import com.ntg.stepcounter.util.extension.foregroundServiceRunning
import com.ntg.stepcounter.util.extension.timber

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, p1: Intent?) {
//        if (!context.foregroundServiceRunning()){
//            val serviceIntent = Intent(context, StepCounterService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent)
//            } else {
//                context.startService(serviceIntent)
//            }
//        }
//
//
//        getActiveNotificationsCount(context)
//        timber("efhjkehfjheskfjhejkfhesjkhfkjes ${getActiveNotificationsCount(context)}")
    }

    private fun getActiveNotificationsCount(context: Context): Int {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.activeNotifications.forEach {
            if (it.id != 1414){
                notificationManager.cancel(it.id)
            }
            try {
                timber("efhjkehfjheskfjhejkfhesjkhfkjes ${it.id} --- ${it.notification.channelId} --- ${it.notification.extras}  --- ${it.describeContents()}")
            }catch (e: Exception){e.printStackTrace()}
        }

        return notificationManager.activeNotifications.size

    }
}