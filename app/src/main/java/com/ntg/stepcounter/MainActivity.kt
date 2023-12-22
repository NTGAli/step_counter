package com.ntg.stepcounter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.stepcounter.api.NetworkResult
import com.ntg.stepcounter.components.CustomButton
import com.ntg.stepcounter.nav.AppNavHost
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.screens.DeadVersionScreen
import com.ntg.stepcounter.ui.theme.Background
import com.ntg.stepcounter.ui.theme.ERROR500
import com.ntg.stepcounter.ui.theme.SECONDARY700
import com.ntg.stepcounter.ui.theme.StepCounterTheme
import com.ntg.stepcounter.ui.theme.fontMedium14
import com.ntg.stepcounter.util.extension.dateOfToday
import com.ntg.stepcounter.util.extension.orFalse
import com.ntg.stepcounter.util.extension.orZero
import com.ntg.stepcounter.util.extension.timber
import com.ntg.stepcounter.vm.LoginViewModel
import com.ntg.stepcounter.vm.MessageViewModel
import com.ntg.stepcounter.vm.SocialNetworkViewModel
import com.ntg.stepcounter.vm.StepViewModel
import com.ntg.stepcounter.vm.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val stepViewModel: StepViewModel by viewModels()
    private val userDataViewModel: UserDataViewModel by viewModels()
    private val socialNetworkViewModel: SocialNetworkViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()
    private var updateId = -1

    companion object {
        lateinit var sensorType: String
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
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

            val navController: NavHostController = rememberNavController()

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                StepCounterTheme {
                    if (startDes.isNotEmpty()) {
                        Scaffold {
                            AppNavHost(
                                navController = navController,
                                stepViewModel = stepViewModel,
                                userDataViewModel = userDataViewModel,
                                socialNetworkViewModel = socialNetworkViewModel,
                                loginViewModel = loginViewModel,
                                messageViewModel = messageViewModel,
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

            DeadVersionCheck{
                startDes = Screens.DeadVersionScreen.name
            }

        }

    }

    @Composable
    private fun DeadVersionCheck(onDead:(Boolean) -> Unit) {
        userDataViewModel.deadCode().collectAsState(initial = -1).let {deadVersion->
            timber("wandnawkdnajkwndkjawdnjkawnd ${deadVersion.value} --- ${BuildConfig.VERSION_CODE}")

            if (deadVersion.value != -1 && deadVersion.value > BuildConfig.VERSION_CODE){
                onDead.invoke(true)
//                DeadVersionScreen(navController)
            }
        }
    }

    private fun syncFcm(uid: String) {
        timber("FCM_UPDATE_START")
        userDataViewModel.getFCM().asLiveData().observe(this) {
            try {
                if (it.isNotEmpty() && it.split("***")[1] == "NotSynced") {
                    userDataViewModel.syncFCM(uid, it.split("***").first()).observe(this) {
                            when (it) {
                                is NetworkResult.Error -> {
                                    timber("FCM_UPDATE_ERROR")
                                    userDataViewModel.setFCM("")
                                }

                                is NetworkResult.Loading -> {
                                    timber("FCM_UPDATE_LOADING")
                                }

                                is NetworkResult.Success -> {
                                    timber("FCM_UPDATE_Success")
                                    if (it.data?.data.orEmpty().isNotEmpty()) {
                                        userDataViewModel.setFCM("${it.data?.data.orEmpty()}***Synced")
                                    }
                                }
                            }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

        if (uid.orEmpty().isNotEmpty()) {

            LaunchedEffect(key1 = uid, block = {
                syncFcm(uid!!)
            })


            if (uid!!.isNotEmpty() && !syncCalled) {
                syncCalled = true
                SyncSteps(stepViewModel, LocalLifecycleOwner.current, uid)
            }

            timeSign =
                userDataViewModel.getTimeSign().collectAsState(initial = "").value

            if (timeSign.isNotEmpty()) {
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




            if (uid.isNotEmpty()) {
                userDataViewModel.userAchievement(uid).observe(LocalLifecycleOwner.current) {
                    when (it) {
                        is NetworkResult.Error -> {

                        }

                        is NetworkResult.Loading -> {

                        }

                        is NetworkResult.Success -> {
                            if (it.data?.data != null) {
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

            if (date.orEmpty().isNotEmpty()) {
                totalSteps = 0
                totalSynced = 0
                steps.forEach { dataStep ->

                    if (dataStep?.count.orZero() > dataStep?.start.orZero()) {
                        totalSteps += dataStep?.count.orZero() - dataStep?.start.orZero()
                        totalSynced = dataStep?.synced.orZero()
                    }

                }

                if ((date != dateOfToday() && totalSteps > totalSynced) || totalSteps - totalSynced > 1) {
                    dateSync = date.orEmpty()
                    if (totalSteps != stepNeedSync) needToSync = true
                    timber("FOREGROUND_SYNC ::: $needToSync")
                }
            }
        }

    }


    timber("FOREGROUND_SYNC $needToSync")

    LaunchedEffect(key1 = needToSync, block = {
        timber("FOREGROUND_SYNC ::: Launched")

        if (dateSync.isNotEmpty()) {
            timber("FOREGROUND_SYNC ::: START ")

            stepViewModel.syncStep(dateSync, totalSteps, uid).observe(owner) {
                when (it) {
                    is NetworkResult.Error -> {
                        timber("FOREGROUND_SYNC ::: ERR")
                    }

                    is NetworkResult.Loading -> {
                        timber("FOREGROUND_SYNC ::: LOADING")
                    }

                    is NetworkResult.Success -> {
                        timber("FOREGROUND_SYNC ::: Success")
                        stepNeedSync = totalSteps
                        if (it.data?.data?.date.orEmpty()
                                .isNotEmpty() && it.data?.data?.count.orZero() != 0
                        ) {
                            stepViewModel.updateSync(
                                it.data?.data?.date.orEmpty(),
                                it.data?.data?.count.orZero()
                            )
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