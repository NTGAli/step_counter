package com.ntg.stepcounter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.ntg.stepcounter.MainActivity.Companion.sensorType
import com.ntg.stepcounter.api.ApiService
import com.ntg.stepcounter.db.AppDB
import com.ntg.stepcounter.models.ResponseBody
import com.ntg.stepcounter.models.Step
import com.ntg.stepcounter.models.UserStore
import com.ntg.stepcounter.models.res.StepSynced
import com.ntg.stepcounter.util.Constants
import com.ntg.stepcounter.util.Constants.NOTIFICATION_CHANNEL_ID
import com.ntg.stepcounter.util.Constants.NOTIFICATION_ID
import com.ntg.stepcounter.util.StepDetector
import com.ntg.stepcounter.util.StepListener
import com.ntg.stepcounter.util.extension.checkInternet
import com.ntg.stepcounter.util.extension.dateOfToday
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.timber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Thread.sleep
import javax.inject.Inject


@AndroidEntryPoint
class MyBackgroundService : Service(), SensorEventListener, LifecycleOwner, StepListener {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification
    private var mSteps: Int = 0
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

    private var stepDetector: StepDetector = StepDetector()

    private var stepSynced = 0
    private var stepsWaitForSync = 0


    @Inject
    lateinit var appDB: AppDB

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var userStore: UserStore

    private var toDayDate = ""
    private var sensorManager: SensorManager? = null
    private var userId = ""
    init {
        steps.value = 0
    }

    override val lifecycle: Lifecycle
        get() = mServiceLifecycleDispatcher.lifecycle

    override fun onCreate() {
        super.onCreate()
        timber("BackgroundService:::onCreate")
        stepDetector.registerListener(this)


        notification = createNotification()
        try {
            startForeground(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        appDB.stepDao().getAllDate().observe(this) { allList ->

            val it = allList.filter { it.date == dateOfToday() }

            updateId = if (it.lastOrNull() != null) {
                it.last().id.toString()
            } else ""

            if (it.isNotEmpty()) {
                toDayDate = it.last().date
                steps.value = 0
            }

            it.forEach { step ->
                if (step.count != 0) {
                    steps.value = steps.value.orZero() + (step.count - step.start.orZero())
                }
            }
        }

        steps.observe(this) {
            try {
                notificationBuilder.setContentText("$it")
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            } catch (e: Exception) {
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
        val accSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        sensorType = if (stepSensor != null) {
            sensorManager!!.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
            Constants.STEP_COUNTER
        } else if (accSensor != null) {
            sensorManager!!.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI)
            Constants.ACCELEROMETER
        } else {
            "NOT SUPPORT"
        }

        val scope = CoroutineScope(Dispatchers.IO)

        runBlocking(Dispatchers.IO) {
            userId = userStore.getUserID.first()
        }


        appDB.stepDao().getUnSyncedStepsOfDate().observe(this@MyBackgroundService) { unSyncedList ->

            val userSteps = unSyncedList.groupBy { it?.date }

            userSteps.forEach { (date, steps) ->

                var totalSteps = 0
                var totalSynced = 0
                steps.forEach {

                    if (it?.count.orZero() > it?.start.orZero()) {
                        totalSteps += it?.count.orZero() - it?.start.orZero()
                        totalSynced = it?.synced.orZero()
                    }

                }

                timber("TOTAL_STEPS_NEED_TO_SYNC DATE = $date :::: steps need to sync = ${totalSteps - totalSynced}")

                if ((date != dateOfToday() && totalSteps != stepsWaitForSync && totalSteps > totalSynced) ||  (totalSteps - totalSynced > 50 && totalSteps != 0 && totalSteps != stepSynced && totalSteps > stepsWaitForSync + 5)) {
                    if (date != null){
                        syncStep(scope, totalSteps, date)
                    }
                }

            }


        }

        return START_NOT_STICKY
    }

    private fun syncStep(scope: CoroutineScope, totalSteps: Int, date: String) {
        stepsWaitForSync = totalSteps
        scope.launch {

            timber("TOTAL_STEPS_NEED_TO_SYNC ::: START")
            if (this@MyBackgroundService.checkInternet()) {
                timber("TOTAL_STEPS_NEED_TO_SYNC ::: INTERNET-OK")

                val call = apiService.syncStepsInBack(date, totalSteps, userId)
                call.enqueue(object : Callback<ResponseBody<StepSynced?>> {
                    override fun onResponse(
                        call: Call<ResponseBody<StepSynced?>>,
                        response: Response<ResponseBody<StepSynced?>>
                    ) {
                        if (response.isSuccessful) {
                            timber("TOTAL_STEPS_NEED_TO_SYNC ::: STEP-SYNCED")
                            val data = response.body()
                            stepSynced = totalSteps
                            if (data?.data?.date != null && data.data.count != null) {
                                scope.launch {
                                    appDB.stepDao()
                                        .updateSync(data.data.date, data.data.count.orZero())
                                }
                            }
                        }
                        stepsWaitForSync = 0
                    }

                    override fun onFailure(
                        call: Call<ResponseBody<StepSynced?>>,
                        t: Throwable
                    ) {
                    }
                })

            }

        }
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
            .setSmallIcon(R.drawable.icons8_trainers_1)
            .setContentTitle(getString(R.string.step_counter))
            .setContentText(getString(R.string.counting))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVibrate(longArrayOf(0))
            .setChannelId(channelId)

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "StepCounterService",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }


    override fun onSensorChanged(p0: SensorEvent?) {

        when (p0?.sensor?.type) {

            Sensor.TYPE_STEP_COUNTER -> {
                timber("onSensorChanged :: Back")
                val count = p0.values?.firstOrNull()?.toInt()
                insertStep(count.orZero())
            }

            Sensor.TYPE_ACCELEROMETER -> {
                stepDetector.updateAccel(p0.timestamp, p0.values[0], p0.values[1], p0.values[2])
            }

        }


    }

    private fun insertStep(count: Int) {
        timber("insertStepInService $count")
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            if (::updateId.isInitialized) {
                if (updateId.isNotEmpty()) {
                    val rowsUpdated =
                        appDB.stepDao().updateCount(updateId.toInt(), dateOfToday(), count, true)
                    if (rowsUpdated == 0) {
                        val newEntity = Step(0, date = dateOfToday(), start = count, exp = true)
                        appDB.stepDao().insert(newEntity)
                    }
                } else {
                    val newEntity = Step(0, date = dateOfToday(), start = count, exp = true)
                    appDB.stepDao().insert(newEntity)
                }
            } else {
                val newEntity = Step(0, date = dateOfToday(), start = count, exp = true)
                updateId = appDB.stepDao().insert(newEntity).toString()
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }



    override fun step(timeNs: Long) {
        mSteps++
        insertStep(mSteps)
    }

    override fun onDestroy() {
        super.onDestroy()
        timber("BackgroundService:::destroy")
        stopSelf()
    }

}


