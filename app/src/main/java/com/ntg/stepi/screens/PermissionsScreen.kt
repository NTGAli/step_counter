package com.ntg.stepi.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ntg.stepi.R
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.components.PermissionItem
import com.ntg.stepi.nav.Screens
import com.ntg.stepi.ui.theme.fontMedium14
import com.ntg.stepi.util.extension.OnLifecycleEvent
import com.ntg.stepi.util.extension.timber
import com.ntg.stepi.vm.UserDataViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PermissionsScreen(
    navHostController: NavHostController,
    userDataViewModel: UserDataViewModel
) {

    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.permissions_and_accesses),
                enableNavigation = false
            )
        },
        content = {
            Content(navHostController = navHostController, userDataViewModel = userDataViewModel)
        }
    )

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Content(navHostController: NavHostController, userDataViewModel: UserDataViewModel) {
    var physicalPermission by remember {
        mutableStateOf(false)
    }

    var notificationStatusPermission by remember {
        mutableStateOf(false)
    }

    var batteryState by remember {
        mutableStateOf(false)
    }

    val physicalActivityPermission =
        rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION, onPermissionResult = {
            physicalPermission = it
        })

    val notificationPermission =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS, onPermissionResult = {
            notificationStatusPermission = it
        })


    val context = LocalContext.current
    val packageName: String = context.packageName
    val pm = context.getSystemService(POWER_SERVICE) as PowerManager?

    batteryState = pm!!.isIgnoringBatteryOptimizations(packageName)
    physicalPermission = physicalActivityPermission.status.isGranted
    notificationStatusPermission = notificationPermission.status.isGranted


    val check = remember {
        mutableStateOf(false)
    }

    val setManually = remember {
        mutableStateOf(false)
    }

    val setManuallyPhysicalActivity = remember {
        mutableStateOf(false)
    }

    setManually.value = !shouldShowRequestPermissionRationale(
        context as Activity,
        Manifest.permission.POST_NOTIFICATIONS
    )

    setManuallyPhysicalActivity.value = !shouldShowRequestPermissionRationale(
        context,
        Manifest.permission.ACTIVITY_RECOGNITION
    )


    Column(modifier = Modifier.padding(horizontal = 32.dp)) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.permissions_desc),
            style = fontMedium14(
                MaterialTheme.colors.secondary
            )
        )

        PermissionItem(modifier = Modifier.padding(top = 24.dp),
            id = 0,
            icon = painterResource(id = R.drawable.icons8_sports_mode_1),
            text = stringResource(
                id = R.string.physical_activity
            ),
            btnText = stringResource(id = R.string.access),
            isGranted = physicalPermission,
            permissionDestination = stringResource(id = R.string.physical_activity_permission_description),
            onClick = {
//                if (setManuallyPhysicalActivity.value){
//                    openAppSettings(context)
//                }else{
//                }
                physicalActivityPermission.launchPermissionRequest()
            })

        PermissionItem(id = 0,
            icon = painterResource(id = R.drawable.notification),
            text = stringResource(
                id = R.string.notification
            ),
            btnText = stringResource(id = R.string.access),
            isGranted = notificationStatusPermission,
            permissionDestination = stringResource(id = R.string.notification_permission_description),
            onClick = {
//                if (setManually.value) {
//                    openAppSettings(context)
//                } else {
//                    notificationPermission.launchPermissionRequest()
//                }
                notificationPermission.launchPermissionRequest()

            })

        PermissionItem(id = 0,
            icon = painterResource(id = R.drawable.battery_charging_full),
            text = stringResource(
                id = R.string.battery
            ),
            btnText = stringResource(id = R.string.access),
            isGranted = batteryState,
            permissionDestination = stringResource(id = R.string.battery_permission_description),
            onClick = {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                context.startActivity(intent)
                check.value = !check.value
            })


        if (checkStartUpPermissionNeed()){
            PermissionItem(id = 0,
                icon = painterResource(id = R.drawable.restart),
                text = stringResource(
                    id = R.string.startup
                ),
                btnText = stringResource(id = R.string.access),
                isGranted = false,
                permissionDestination = stringResource(id = R.string.startup_permission_description),
                onClick = {
                    addAutoStartup(context){
                        userDataViewModel.isAutoStart(true)
                    }
                })
        }


    }

    OnLifecycleEvent(onEvent = { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            batteryState = pm.isIgnoringBatteryOptimizations(packageName)
            if (physicalPermission && notificationStatusPermission && batteryState) {
                navHostController.navigate(Screens.HomeScreen.name)
            }
        }
    })


}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}

fun checkStartUpPermissionNeed(): Boolean {
    val manufacturer = Build.MANUFACTURER
    return ("xiaomi".equals(manufacturer, ignoreCase = true)) || ("oppo".equals(
        manufacturer,
        ignoreCase = true
    )) || ("vivo".equals(manufacturer, ignoreCase = true)) || ("Letv".equals(
        manufacturer,
        ignoreCase = true
    )) || ("Honor".equals(manufacturer, ignoreCase = true))
}

private fun addAutoStartup(context: Context, onClick:() -> Unit) {
    try {
        val intent = Intent()
        val manufacturer = Build.MANUFACTURER
        if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
            onClick.invoke()
            intent.setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            )
        } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
            onClick.invoke()
            intent.setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            )
        } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
            onClick.invoke()
            intent.setComponent(
                ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            )
        } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
            onClick.invoke()
            intent.setComponent(
                ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )
            )
        } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
            onClick.invoke()
            intent.setComponent(
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            )
        }
        val list: List<ResolveInfo> =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.isNotEmpty()) {
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        timber("startup permission :::: ${e.printStackTrace()}")
    }
}