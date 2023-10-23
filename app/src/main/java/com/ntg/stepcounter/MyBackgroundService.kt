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
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.ntg.stepcounter.db.AppDB
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.util.Constants.NOTIFICATION_CHANNEL_ID
import com.ntg.stepcounter.util.extension.dateOfToday
import com.ntg.stepcounter.util.extension.orZero
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
    private lateinit var updateId: String
    private var steps: MutableLiveData<Int> = MutableLiveData()


    @Inject
    lateinit var appDB: AppDB

    private var sensorManager: SensorManager? = null


    override fun onCreate() {
        super.onCreate()
        timber("BackgroundService:::onCreate")
        notification = createNotification()
//        notificationManager.notify(1414, notification)
        try {
            startForeground(1414, notification)
        }catch (e: Exception) {e.printStackTrace()}


        appDB.stepDao().getToday(dateOfToday()).observe(this){
            updateId = if (it.lastOrNull() != null){
                it.last().id.toString()
            } else ""

            timber("aklwjdaklwjdlkawjdlkwlkd $updateId")

            steps.value = 0
            it.forEach { step ->
                if (step.count != 0){
                    steps.value = steps.value.orZero() + (step.count - step.start.orZero())
                }
            }


        }
    }

    private fun updateNotification(){
        steps.observe(this){
            try {
                notificationBuilder.setContentText(it.toString())
                notificationManager.notify(1414, notificationBuilder.build())
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timber("BackgroundService:::start")
        mServiceLifecycleDispatcher.onServicePreSuperOnStart()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor != null) {
            sensorManager!!.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
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
            .setVibrate(longArrayOf(0))
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



    override fun onSensorChanged(p0: SensorEvent?) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            timber("onSensorChanged :::: Background")

            val count = p0?.values?.firstOrNull()?.toInt()


           if (::updateId.isInitialized){
               if (updateId.isNotEmpty()){
                   val rowsUpdated = appDB.stepDao().updateCount(updateId.toInt(), count, true)
                   if (rowsUpdated == 0) {
                       val newEntity = Step(0,date = dateOfToday(), start  =  count, exp = true)
                       appDB.stepDao().insert(newEntity)
                   }
               }else{
                   val newEntity = Step(0,date = dateOfToday(), start  =  count, exp = true)
                   appDB.stepDao().insert(newEntity)
               }
           }

//            val dateOfToday = dateOfToday()
//            val rowsUpdated = appDB.stepDao().updateCount(dateOfToday)
//            if (rowsUpdated == 0) {
//                val newEntity = Step(0,date = dateOfToday, count =  1)
//                appDB.stepDao().insert(newEntity)
//            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        timber("BackgroundService:::destroy")
        sensorManager?.unregisterListener(this)
        sensorManager = null
        stopSelf()
    }

    override val lifecycle: Lifecycle
        get() = mServiceLifecycleDispatcher.lifecycle
}


