package com.ntg.stepcounter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material.Text
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ntg.stepcounter.screens.BottomSheetTest
import com.ntg.stepcounter.ui.theme.StepCounterTheme
import com.ntg.stepcounter.vm.StepViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val stepViewModel: StepViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            val ctx = LocalContext.current
            val serviceIntent = Intent(ctx, MyBackgroundService::class.java)
            startService(serviceIntent)
//            startService(
//                Intent(
//                    this@GPSLoc,
//                    MyServiceNotifications::class.java
//                )
//            ) //enciendo el service


            StepCounterTheme {
                BottomSheetTest(stepViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StepCounterTheme {
        Greeting("Android")
    }
}