package com.ntg.stepcounter.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ntg.stepcounter.MainActivity
import com.ntg.stepcounter.R
import com.ntg.stepcounter.screens.startServiceViaWorker
import com.ntg.stepcounter.services.StepCounterService
import com.ntg.stepcounter.util.extension.foregroundServiceRunning
import com.ntg.stepcounter.util.extension.timber

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        timber("MyFirebaseMessagingService ::: FROM : ${remoteMessage.from}")

        val data = remoteMessage.data
        val myCustomKey = data["stepService"]

        if (myCustomKey == "start"){
            startStepService()
        }else if (remoteMessage.notification?.title.orEmpty().isNotEmpty()) {
            sendNotification(
                remoteMessage.notification?.title.orEmpty(),
                remoteMessage.notification?.body.orEmpty(),
                remoteMessage.data["action"].orEmpty()
            )
        }



    }

    override fun onNewToken(token: String) {
        timber("FCM_TOKEN :::: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        timber("sendRegistrationTokenToServer $token")
    }

    private fun startStepService(){
        if (!foregroundServiceRunning()) {
            val serviceIntent = Intent(this, StepCounterService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
                timber("isInBackgroundStarted ::: ForegroundService")
            } else {
                startService(serviceIntent)
                timber("isInBackgroundStarted ::: Service")
            }
            startServiceViaWorker(this)
        }
    }

    private fun sendNotification(title: String, messageBody: String, action: String) {

        val intent = if (action.startsWith("http")){
            Intent(Intent.ACTION_VIEW)
        }else Intent(this, MainActivity::class.java)

        if (action.startsWith("http")){
            intent.data  = Uri.parse(action)
        }else{
        intent.putExtra(Constants.ACTION, action)
        intent.action = action
        }
        timber("CUSTOM_DATA_NOTIFICATION ::::::::::::::: $action")

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icons8_sneakers_1)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

}