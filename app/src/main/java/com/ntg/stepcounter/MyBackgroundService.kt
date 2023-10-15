package com.ntg.stepcounter

import android.Manifest
import android.R.id
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.ntg.stepcounter.db.AppDB
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.util.Constants.NOTIFICATION_CHANNEL_ID
import com.ntg.stepcounter.util.extension.dateOfToday
import com.ntg.stepcounter.util.extension.timber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class MyBackgroundService : Service(), SensorEventListener, LifecycleOwner {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val mServiceLifecycleDispatcher = ServiceLifecycleDispatcher(this)
    private val channelId =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NOTIFICATION_CHANNEL_ID
        } else {
            // If running on older Android versions, use a default channel ID
            ""
        }

    @Inject
    lateinit var appDB: AppDB

    private var sensorManager: SensorManager? = null


    override fun onCreate() {
        super.onCreate()
        timber("BackgroundService:::onCreate")
        notification = createNotification()
//        notificationManager.notify(1414, notification)
        startForeground(1414, notification)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timber("BackgroundService:::start")
        mServiceLifecycleDispatcher.onServicePreSuperOnStart()
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }


        appDB.stepDao().getToday(dateOfToday()).observe(this){

            try {
                timber("klajwdlkajdlkajwlkdjlwkad ${it.count}")
                notificationBuilder.setContentText(it.count.toString())
                notificationManager.notify(1414, notificationBuilder.build())
//                notification.notify()
            }catch (e: Exception){
                e.printStackTrace()
            }

        }

        return START_NOT_STICKY
    }




    private fun createNotification(): Notification {


        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )

        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icons8_trainers_1) // Replace with your notification icon
//            .setContentTitle("Step Counting Service")
//            .setContentText("Counting your steps...")
            .setContentTitle("قدم شمار")
            .setContentText("در حال شمارش ..")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Set the PendingIntent when the notification is clicked
            .setOngoing(true) // Makes the notification persistent
            .setAutoCancel(false) // Prevents the notification from being dismissed when clicked
//
//        // Create a notification channel (if needed) for Android Oreo and later
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            val channel = NotificationChannel(
//                channelId,
//                "Step Counting Service",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
        return notificationBuilder.build()
    }


    private fun createNotification2(context: Context) {
        val builder = NotificationCompat.Builder(context, "123")
            .setSmallIcon(androidx.core.R.drawable.ic_call_answer_low)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(123, builder.build())
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            timber("StepCounterListener :::: Background")
            val dateOfToday = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().toString()
            } else {
                val currentDate = Date()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                dateFormat.format(currentDate)
            }
            val rowsUpdated = appDB.stepDao().updateCount(dateOfToday)
            if (rowsUpdated == 0) {
                // If no rows were updated, insert a new row with count = 1
                val newEntity = Step(0,date = dateOfToday, count =  1) // Replace with your entity constructor
                appDB.stepDao().insert(newEntity)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        timber("BackgroundService:::destroy")
        sensorManager = null
        stopSelf()
    }

    override val lifecycle: Lifecycle
        get() = mServiceLifecycleDispatcher.lifecycle
}


