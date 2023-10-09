package com.ntg.stepcounter

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import com.ntg.stepcounter.nav.AppNavHost
import com.ntg.stepcounter.ui.theme.StepCounterTheme
import com.ntg.stepcounter.util.extension.OnLifecycleEvent
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.vm.LoginViewModel
import com.ntg.stepcounter.vm.SocialNetworkViewModel
import com.ntg.stepcounter.vm.StepViewModel
import com.ntg.stepcounter.vm.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), SensorEventListener {

    private val stepViewModel: StepViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val socialNetworkViewModel: SocialNetworkViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private var sensorManager: SensorManager? = null
    private var isInBackground = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }



        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                StepCounterTheme {
                    AppNavHost(
                        stepViewModel = stepViewModel,
                        userDataViewModel = userDataViewModel,
                        socialNetworkViewModel = socialNetworkViewModel,
                        loginViewModel = loginViewModel,
                        onDestinationChangedListener = { nav, des, bundle ->

                        })
                }
            }
            HandleLifecycle(
                startOnBackground = userDataViewModel.isAutoDetect()
                    .collectAsState(initial = true).value
            ) {
                if (it == Lifecycle.Event.ON_PAUSE) {
                    isInBackground = true
                } else if (it == Lifecycle.Event.ON_RESUME) {
                    isInBackground = false
                }
            }
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (!isInBackground) {
            timber("StepCounterListener :::: Forground")
            stepViewModel.insertStep()
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}


@Composable
private fun HandleLifecycle(
    startOnBackground: Boolean,
    onEventChange: (Lifecycle.Event) -> Unit = {}
) {
    val ctx = LocalContext.current
    val serviceIntent = Intent(ctx, MyBackgroundService::class.java)

    val events = remember {
        mutableStateOf(Lifecycle.Event.ON_START)
    }

    OnLifecycleEvent { owner, event ->
        events.value = event
        onEventChange.invoke(event)
    }


    when (events.value) {
        Lifecycle.Event.ON_START -> {
        }

        Lifecycle.Event.ON_RESUME -> {
            ctx.stopService(serviceIntent)
        }

        Lifecycle.Event.ON_STOP -> {
        }

        Lifecycle.Event.ON_PAUSE -> {
            if (startOnBackground) {
                ctx.startService(serviceIntent)
            }
        }

        Lifecycle.Event.ON_DESTROY -> {
        }

        else -> {}
    }


}