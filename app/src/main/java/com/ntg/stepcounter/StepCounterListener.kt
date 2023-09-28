package com.ntg.stepcounter

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class StepCounterListener(private val onSensorChangedCallback:(SensorEvent?) -> Unit): SensorEventListener {

    private var sensorManager: SensorManager? = null

    fun setup(context: Context){

        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
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
    }


    override fun onSensorChanged(p0: SensorEvent?) {
        Log.d("dwd","wdlwkjkdlkwjdlkwad 111 $p0")
        onSensorChangedCallback.invoke(p0)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}