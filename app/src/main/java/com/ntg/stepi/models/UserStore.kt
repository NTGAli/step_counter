package com.ntg.stepi.models

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ntg.stepi.util.extension.timber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        private val USER_TOKEN = stringPreferencesKey("userToken")
        private val FCM_TOKEN = stringPreferencesKey("fcmToken")
        private val USER_NAME = stringPreferencesKey("username")
        private val USER_ID = stringPreferencesKey("userId")
        private val PHONE_NUMBER = stringPreferencesKey("phoneNumber")
        private val STATUS = stringPreferencesKey("status")
        private val FIELD_STUDY = stringPreferencesKey("field_study")
        private val IS_VERIFIED = booleanPreferencesKey("isVerified")
        private val IS_BLOCKED = booleanPreferencesKey("isBlocked")
        private val SHOW_REPORT = booleanPreferencesKey("showReport")
        private val AUTO_DETECT = booleanPreferencesKey("auto_detect")
        private val AUTO_START = booleanPreferencesKey("auto_start")
        private val GRADE_ID = intPreferencesKey("grade_id")
        private val FOS_ID = intPreferencesKey("fos_id")
        private val TIME_SIGN = stringPreferencesKey("time_sign")
        private val ACHIEVEMENT = stringPreferencesKey("achievement")
        private val CLAPS = intPreferencesKey("claps")
        private val THEME = stringPreferencesKey("theme")
        private val MESSAGES_ID = stringPreferencesKey("messagesId")
        private val DEAD_CODE = intPreferencesKey("dead_code")
        private val LANGUAGE = stringPreferencesKey("language")
    }

    val getUserToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN] ?: ""
    }

    val getFCMToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[FCM_TOKEN] ?: ""
    }

    val getDeadCode: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DEAD_CODE] ?: -1
    }

    val getTheme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME] ?: "default"
    }

    val getLanguage: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "fa"
    }

    val getTimeSign: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TIME_SIGN] ?: ""
    }

    val getAchievement: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ACHIEVEMENT] ?: ""
    }

    val getUserName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: ""
    }

    val getUserID: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_ID] ?: ""
    }

    val getGradeId: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[GRADE_ID] ?: -1
    }

    val getClaps: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CLAPS] ?: -1
    }

    val getFosId: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FOS_ID] ?: -1
    }

    val getPhoneNumber: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PHONE_NUMBER] ?: ""
    }

    val getStatus: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[STATUS] ?: ""
    }

    val isVerified: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_VERIFIED] ?: false
    }

    val isBlocked: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_BLOCKED] ?: false
    }

    val fieldStudy: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[FIELD_STUDY] ?: ""
    }

    val messagesId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[MESSAGES_ID] ?: ""
    }

    val showReport: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SHOW_REPORT] ?: true
    }

    val isAutoDetect: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_DETECT] ?: true
    }

    val isAutoStart: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_START] ?: false
    }


    suspend fun clearUserData() = context.dataStore.edit { it.clear() }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }


    suspend fun saveFCMToken(token: String) {
        context.dataStore.edit { preferences ->
            timber("Current_FCM ::: ${preferences[FCM_TOKEN]} -- Correct_FCM :::: $token ")
            if (preferences[FCM_TOKEN]?.split("***")?.first() != token.split("***").first())
                preferences[FCM_TOKEN] = token
        }
    }

    suspend fun saveTimeSign(timeSing: String) {
        context.dataStore.edit { preferences ->
            preferences[TIME_SIGN] = timeSing
        }
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = username
        }
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME] = theme
        }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    suspend fun saveUserID(userID: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userID
        }
    }

    suspend fun saveDeadCode(deadCode: Int) {
        context.dataStore.edit { preferences ->
            preferences[DEAD_CODE] = deadCode
        }
    }


    suspend fun savePhoneNumber(phoneNumber: String) {
        context.dataStore.edit { preferences ->
            preferences[PHONE_NUMBER] = phoneNumber
        }
    }

    suspend fun saveSTATUS(status: String) {
        context.dataStore.edit { preferences ->
            preferences[STATUS] = status
        }
    }

    suspend fun saveFiledStudy(fieldStudy: String) {
        context.dataStore.edit { preferences ->
            preferences[FIELD_STUDY] = fieldStudy
        }
    }

    suspend fun saveMessageIds(messageIds: String) {
        context.dataStore.edit { preferences ->
            preferences[MESSAGES_ID] = messageIds
        }
    }

    suspend fun saveAchievement(achievement: String) {
        context.dataStore.edit { preferences ->
            preferences[ACHIEVEMENT] = achievement
        }
    }

    suspend fun setGradeId(gradeId: Int) {
        context.dataStore.edit { preferences ->
            preferences[GRADE_ID] = gradeId
        }
    }

    suspend fun setClaps(claps: Int) {
        context.dataStore.edit { preferences ->
            preferences[CLAPS] = claps
        }
    }

    suspend fun setFosId(gradeId: Int) {
        context.dataStore.edit { preferences ->
            preferences[FOS_ID] = gradeId
        }
    }

    suspend fun isVerified(isVerified: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_VERIFIED] = isVerified
        }
    }

    suspend fun isBlocked(isBlocked: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_BLOCKED] = isBlocked
        }
    }

    suspend fun isShowReport(isShow: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_REPORT] = isShow
        }
    }

    suspend fun isAutoDetect(isAuto: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_DETECT] = isAuto
        }
    }

    suspend fun isAutoStart(isAuto: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_START] = isAuto
        }
    }
}