package com.ntg.stepcounter

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
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
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ntg.stepcounter.db.AppDB
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.util.Constants.NOTIFICATION_CHANNEL_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ScheduledExecutorService
import javax.inject.Inject


@AndroidEntryPoint
class MyBackgroundService : Service(), SensorEventListener {

    @Inject
    lateinit var appDB: AppDB

    private lateinit var scheduledExecutorService: ScheduledExecutorService

    private val onCh:(Int) -> Unit = {}

    override fun onCreate() {
        super.onCreate()
//        createNotification(this)
        Timber.d("ksjkdjzslkjdlkajdlkjwlkdjw 123")
        Toast.makeText(this,"wwwwwwwwwwwwww",Toast.LENGTH_SHORT).show()

        val notification = createNotification(this)
        startForeground(1111, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        // You can return null because you don't need to bind to this service.
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Your background task code goes here.
        // You can start a new thread or use coroutines for asynchronous tasks.
        val scope = CoroutineScope(Dispatchers.IO)

//        scheduledExecutorService = Executors.newScheduledThreadPool(1)
//        scheduledExecutorService.scheduleAtFixedRate({
//
//
//
//
//        },1L,1,TimeUnit.SECONDS)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)


        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
//            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
            Log.d("dwd","wdlwkjkdlkwjdlkwad null $stepSensor")
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        Timber.d("ksjkdjzslkjdlkajdlkjwlkdjw 456")

        StepCounterListener{
            scope.launch {
                Toast.makeText(this@MyBackgroundService, "111", Toast.LENGTH_SHORT).show()
                appDB.stepDao().insert(Step(0, System.currentTimeMillis().toString(), true))
            }
        }


        // To stop the service when the task is done, call stopSelf().
//        stopSelf()




        // Return a value to specify how the service should behave.
        return START_NOT_STICKY
    }



    private fun createNotification(context: Context): Notification {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NOTIFICATION_CHANNEL_ID
            } else {
                // If running on older Android versions, use a default channel ID
                ""
            }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your notification icon
            .setContentTitle("Step Counting Service")
            .setContentText("Counting your steps...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Set the PendingIntent when the notification is clicked
            .setOngoing(true) // Makes the notification persistent
            .setAutoCancel(false) // Prevents the notification from being dismissed when clicked

        // Create a notification channel (if needed) for Android Oreo and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                "Step Counting Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }


    private fun createNotification2(context: Context) {
        var builder = NotificationCompat.Builder(context, "123")
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
        Log.d("kjwd", "kaljdlkwajdkljwalkdjwk ${p0!!.values[0]}")
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
//            Toast.makeText(this@MyBackgroundService, "111", Toast.LENGTH_SHORT).show()
            appDB.stepDao().insert(Step(0, System.currentTimeMillis().toString(), true))
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}


