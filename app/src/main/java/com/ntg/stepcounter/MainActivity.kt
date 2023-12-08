package com.ntg.stepcounter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
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
import com.ntg.stepcounter.util.StepDetector
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
class MainActivity : ComponentActivity() {

    private val stepViewModel: StepViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val socialNetworkViewModel: SocialNetworkViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private var updateId = -1
    private lateinit var stepDetector: StepDetector

    companion object{
        lateinit var sensorType: String
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        stepDetector = StepDetector()
        super.onCreate(savedInstanceState)
        setContent {

            stepViewModel.getToday().observeAsState().value?.lastOrNull().let {
                updateId = if (it != null && it.start.orZero() != 0) it.id else -1
            }

            var startDes by remember {
                mutableStateOf("")
            }

            userDataViewModel.getUsername().collectAsState(initial = null).value.let {
                if (it != null) {
                    startDes = if (it.isNotEmpty()) {
                        checkPermissions()
                    } else Screens.LoginScreen.name
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                StepCounterTheme {
                    if (startDes.isNotEmpty()) {
                        Scaffold {
                            AppNavHost(
                                stepViewModel = stepViewModel,
                                userDataViewModel = userDataViewModel,
                                socialNetworkViewModel = socialNetworkViewModel,
                                loginViewModel = loginViewModel,
                                startDestination = startDes,
                                onDestinationChangedListener = { _, des, bundle ->
                                    timber("onDestinationChangedListener ::: ${des.route} :: ${bundle.toString()}")
                                })
                        }
                    }
                }
            }


            userDataViewModel.getUserId().collectAsState(initial = null).let {
                SetupDataUser(it.value)

            }

            val isBlocked = userDataViewModel.isBlocked().collectAsState(initial = false).value

            if (isBlocked) {
                UserBlocked(blockReasons = getString(R.string.block_user))
            }
        }
    }

    @Composable
    fun SetupDataUser(uid: String?) {
        timber("SetupDataUser :::: UID :::: $uid")
        var syncCalled by remember {
            mutableStateOf(false)
        }

        var timeSign by remember {
            mutableStateOf("")
        }



        if (uid.orEmpty().isNotEmpty()){
            timber("isInBackgroundStarted ::: UID ::: $uid")
            val serviceIntent = Intent(this, StepCounterService::class.java)
            startService(serviceIntent)
        }




        if (uid != null) {
            if (uid.isNotEmpty() && !syncCalled) {
                syncCalled = true
                SyncSteps(stepViewModel, LocalLifecycleOwner.current, uid)
            }

            timeSign =
                userDataViewModel.getTimeSign().collectAsState(initial = "").value

            if(timeSign.isNotEmpty()){
                userDataViewModel.accountState(uid)
                    .observe(LocalLifecycleOwner.current) {
                        when (it) {
                            is NetworkResult.Error -> {

                            }

                            is NetworkResult.Loading -> {

                            }

                            is NetworkResult.Success -> {
                                if (it.data?.isSuccess.orFalse()) {
                                    userDataViewModel.isVerified(it.data?.data?.isVerified.orFalse())
                                    userDataViewModel.isBlocked(it.data?.data?.isBlock.orFalse())
                                }
                            }
                        }
                    }
            }




            if (uid.isNotEmpty()){
                userDataViewModel.userAchievement(uid).observe(LocalLifecycleOwner.current){
                    when(it){
                        is NetworkResult.Error -> {

                        }
                        is NetworkResult.Loading -> {

                        }
                        is NetworkResult.Success -> {
                            if (it.data?.data != null){
                                userDataViewModel.setAchievement(Gson().toJson(it.data.data))
                            }
                        }
                    }
                }
            }

        }


    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun checkPermissions(): String {
    val context = LocalContext.current
    val physicalActivityPermission =
        rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION).status.isGranted

    val notificationPermission =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS).status.isGranted
    val packageName: String = context.packageName
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager?

    val batteryState = pm!!.isIgnoringBatteryOptimizations(packageName)

    return if (physicalActivityPermission && notificationPermission && batteryState) Screens.HomeScreen.name
    else Screens.PermissionScreen.name
}

@Composable
private fun SyncSteps(stepViewModel: StepViewModel, owner: LifecycleOwner, uid: String) {
    timber("StepSync")
    var stepNeedSync = 0
    var totalSteps = 0
    var totalSynced: Int
    var dateSync = ""

    var needToSync by remember {
        mutableStateOf(false)
    }

    stepViewModel.needToSyncSteps().observe(owner) {

        val groupedSteps = it.groupBy { userSteps -> userSteps?.date }

        groupedSteps.forEach { (date, steps) ->

            if (date.orEmpty().isNotEmpty()){
                totalSteps = 0
                totalSynced = 0
                steps.forEach {dataStep ->

                    if (dataStep?.count.orZero() > dataStep?.start.orZero()){
                        totalSteps += dataStep?.count.orZero() - dataStep?.start.orZero()
                        totalSynced = dataStep?.synced.orZero()
                    }

                }

                if ((date != dateOfToday() && totalSteps > totalSynced) || totalSteps - totalSynced > 1){
                    dateSync = date.orEmpty()
                    if (totalSteps != stepNeedSync) needToSync= true
                    timber("FOREGROUND_SYNC ::: $needToSync")
                }
            }
        }

    }


    timber("FOREGROUND_SYNC $needToSync")

    LaunchedEffect(key1 = needToSync, block = {
        timber("FOREGROUND_SYNC ::: Launched")

        if (dateSync.isNotEmpty()){
            timber("FOREGROUND_SYNC ::: START ")

            stepViewModel.syncStep(dateSync, totalSteps, uid).observe(owner){
                when(it){
                    is NetworkResult.Error -> {
                        timber("FOREGROUND_SYNC ::: ERR")
                    }
                    is NetworkResult.Loading -> {
                        timber("FOREGROUND_SYNC ::: LOADING")
                    }
                    is NetworkResult.Success -> {
                        timber("FOREGROUND_SYNC ::: Success")
                        stepNeedSync = totalSteps
                        if (it.data?.data?.date.orEmpty().isNotEmpty() && it.data?.data?.count.orZero() != 0){
                            stepViewModel.updateSync(it.data?.data?.date.orEmpty(), it.data?.data?.count.orZero())
                            stepViewModel.clearSummaries()
                            needToSync = false
                        }
                    }
                }
            }

        }
    })
}


@Composable
private fun UserBlocked(
    blockReasons: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            modifier = Modifier.padding(top = 164.dp),
            painter = painterResource(id = R.drawable.alert_triangle),
            contentDescription = null,
            tint = ERROR500
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = blockReasons,
            style = fontMedium14(SECONDARY700)
        )

        CustomButton(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.wrong),
            style = ButtonStyle.TextOnly
        )

    }
}

fun logout(
    userDataViewModel: UserDataViewModel,
    stepViewModel: StepViewModel,
    socialNetworkViewModel: SocialNetworkViewModel
) {
    stepViewModel.clearUserSteps()
    socialNetworkViewModel.clearAllSocials()
    userDataViewModel.clearUserData()
}