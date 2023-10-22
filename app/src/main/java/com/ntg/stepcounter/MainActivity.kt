package com.ntg.stepcounter

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.nav.AppNavHost
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.Background
import com.ntg.stepcounter.ui.theme.ERROR500
import com.ntg.stepcounter.ui.theme.SECONDARY700
import com.ntg.stepcounter.ui.theme.StepCounterTheme
import com.ntg.stepcounter.ui.theme.fontMedium14
import com.ntg.stepcounter.util.extension.OnLifecycleEvent
import com.ntg.stepcounter.util.extension.dateOfToday
import com.ntg.stepcounter.util.extension.orFalse
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
    private var updateId = -1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        super.onCreate(savedInstanceState)
        setContent {

            updateId = stepViewModel.getToday().observeAsState().value?.lastOrNull()?.id ?: -1

            var startDes by remember {
                mutableStateOf("")
            }

            userDataViewModel.getUsername().collectAsState(initial = null).value.let {
                if (it != null){
                    startDes = if (it.isNotEmpty()) Screens.HomeScreen.name
                    else Screens.LoginScreen.name
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                StepCounterTheme {
                    if (startDes.isNotEmpty()){
                        timber("ajwhdklawjkdjawlkjdlakw $startDes")
                        AppNavHost(
                            stepViewModel = stepViewModel,
                            userDataViewModel = userDataViewModel,
                            socialNetworkViewModel = socialNetworkViewModel,
                            loginViewModel = loginViewModel,
                            startDestination = startDes,
                            onDestinationChangedListener = { nav, des, bundle ->

                            })
                    }
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


            userDataViewModel.getUserId().collectAsState(initial = "").let {
                if (it.value.isNotEmpty()){
                    syncSteps(stepViewModel, LocalLifecycleOwner.current, it.value)
                }

                userDataViewModel.accountState(it.value).observe(LocalLifecycleOwner.current){
                    when(it){
                        is NetworkResult.Error -> {

                        }
                        is NetworkResult.Loading -> {

                        }
                        is NetworkResult.Success -> {
                            if (it.data?.isSuccess.orFalse()){
                                userDataViewModel.isVerified(it.data?.data?.isVerified.orFalse())
                                userDataViewModel.isBlocked(it.data?.data?.isBlock.orFalse())
                            }
                        }
                    }
                }
            }

            val isBlocked = userDataViewModel.isBlocked().collectAsState(initial = false).value

            if (isBlocked){
                UserBlocked(blockReasons = "حساب کاربری شما مسدود شده است!")
            }

        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        timber("onSensorChanged FOR")
        stepViewModel.insertStep(p0?.values?.firstOrNull().orZero().toInt(), updateId)
        if (!isInBackground) {
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}

private fun syncSteps(stepViewModel: StepViewModel, owner: LifecycleOwner, userPhone: String) {
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
                                stepViewModel.clearSummaries()
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
//            ctx.stopService(serviceIntent)
        }

        Lifecycle.Event.ON_STOP -> {
        }

        Lifecycle.Event.ON_PAUSE -> {
            if (startOnBackground) {
//                ctx.startService(serviceIntent)
            }
        }

        Lifecycle.Event.ON_DESTROY -> {
        }

        else -> {}
    }


}

@Composable
private fun UserBlocked(
    blockReasons: String
){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Background), horizontalAlignment = Alignment.CenterHorizontally) {

        Icon(modifier = Modifier.padding(top = 164.dp), painter = painterResource(id = R.drawable.alert_triangle), contentDescription = null, tint = ERROR500 )

        Text(modifier = Modifier.padding(top = 16.dp), text = blockReasons, style = fontMedium14(SECONDARY700))

        CustomButton(modifier = Modifier.padding(top = 16.dp), text = stringResource(id = R.string.wrong), style = ButtonStyle.TextOnly)

    }
}