package com.ntg.stepi

import android.app.Application
import android.text.TextUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.ntg.stepi.api.ApiService
import com.ntg.stepi.models.UserStore
import com.ntg.stepi.util.extension.timber
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class CounterApp: Application() {

    @Inject
    lateinit var apiService: ApiService

    private lateinit var userStore: UserStore


    override fun onCreate() {
        super.onCreate()
        initTimber()
        userStore = UserStore(this)
        fcmCheck()
        subscribeTopic()
    }

    private fun subscribeTopic() {
        Firebase.messaging.subscribeToTopic("stepService")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                timber("subscribeToTopic :::: $msg")
            }
    }

    private fun fcmCheck(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {
                timber("FCM_TOKEN . ::::: $token")
                saveFCM(token)
            } else {
                timber("FCM_TOKEN . ::::: NULL TOKEN")
            }
        }.addOnFailureListener { e: Exception? -> }.addOnCanceledListener {}
            .addOnCompleteListener { task: Task<String> ->
                try {
                    timber("FCM_TOKEN_FAIL . ::::: ${task.result}")
                    saveFCM(task.result)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
    }

    private fun saveFCM(token: String){
        runBlocking {
            withContext(Dispatchers.IO){
                userStore.saveFCMToken("$token***NotSynced")
            }
        }
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}