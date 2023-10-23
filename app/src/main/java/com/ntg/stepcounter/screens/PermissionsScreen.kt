package com.ntg.stepcounter.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ntg.stepcounter.R
import com.ntg.stepcounter.components.Appbar
import com.ntg.stepcounter.components.PermissionItem
import com.ntg.stepcounter.nav.Screens
import com.ntg.stepcounter.ui.theme.SECONDARY800
import com.ntg.stepcounter.ui.theme.fontMedium14


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PermissionsScreen(
    navHostController: NavHostController
) {

    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.permissions_and_accesses),
                enableNavigation = false
            )
        },
        content = {
            Content(navHostController = navHostController)
        }
    )

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Content(navHostController: NavHostController) {
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
                SECONDARY800
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
            onClick = {
                if (setManuallyPhysicalActivity.value){
                    openAppSettings(context)
                }else{
                    physicalActivityPermission.launchPermissionRequest()
                }
            })

        PermissionItem(id = 0,
            icon = painterResource(id = R.drawable.icons8_notification_1),
            text = stringResource(
                id = R.string.notification
            ),
            btnText = stringResource(id = R.string.access),
            isGranted = notificationStatusPermission,
            onClick = {
                if (setManually.value) {
                    openAppSettings(context)
                } else {
                    notificationPermission.launchPermissionRequest()
                }
            })

        PermissionItem(id = 0,
            icon = painterResource(id = R.drawable.icons8_batteries_1),
            text = stringResource(
                id = R.string.battery
            ),
            btnText = stringResource(id = R.string.access),
            isGranted = batteryState,
            onClick = {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                context.startActivity(intent)
            })


    }

//    if (physicalPermission && notificationStatusPermission && batteryState){
//        navHostController.navigate(Screens.HomeScreen.name){
//            popUpTo(0)
//        }
//    }

}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}