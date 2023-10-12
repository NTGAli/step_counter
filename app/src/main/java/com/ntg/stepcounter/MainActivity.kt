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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.nav.AppNavHost
import com.ntg.stepcounter.ui.theme.StepCounterTheme
import com.ntg.stepcounter.util.extension.OnLifecycleEvent
import com.ntg.stepcounter.util.extension.dateOfToday
import com.ntg.stepcounter.util.extension.orZero
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

            userDataViewModel.setPhone("09399085012")

            userDataViewModel.getPhoneNumber().collectAsState(initial = "").let {

                timber("ahdjwkhdjakwhdjkhwakjhdkw 22 ${it.value}")
                if (it.value.isNotEmpty()){
                    timber("ahdjwkhdjakwhdjkhwakjhdkw 11")
                    syncSteps(stepViewModel, LocalLifecycleOwner.current, it.value)
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

private fun syncSteps(stepViewModel: StepViewModel, owner: LifecycleOwner, userPhone: String) {
    timber("ahdjwkhdjakwhdjkhwakjhdkw")
    stepViewModel.needToSyncSteps().observe(owner) {
        it.forEach { step ->
            if (step != null && (step.date != dateOfToday() || (step.date == dateOfToday() && (step.count.orZero() - step.synced.orZero() >= 10)))) {
                stepViewModel.syncStep(userPhone, step).observe(owner) {
                    when (it) {
                        is NetworkResult.Error -> {
                            timber("StepSync ::: ERR")
                        }

                        is NetworkResult.Loading -> {
                            timber("StepSync ::: Loading")
                        }

                        is NetworkResult.Success -> {
                            timber("StepSync ::: Success ::: ${it.data?.data}")
                            if (it.data?.data?.date != null && it.data.data.count != null){
                                stepViewModel.updateSync(it.data.data.date, it.data.data.count)
                            }
                        }
                    }
                }
            }
        }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ctx.startForegroundService(serviceIntent)
                } else {
                    ctx.startService(serviceIntent)
                }
            }
        }

        Lifecycle.Event.ON_DESTROY -> {
        }

        else -> {}
    }


}