package com.ntg.stepcounter.models

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        private val USER_TOKEN = stringPreferencesKey("userToken")
        private val USER_NAME = stringPreferencesKey("username")
        private val USER_ID = stringPreferencesKey("userId")
        private val PHONE_NUMBER = stringPreferencesKey("phoneNumber")
        private val STATUS = stringPreferencesKey("status")
        private val FIELD_STUDY = stringPreferencesKey("field_study")
        private val IS_VERIFIED = booleanPreferencesKey("isVerified")
        private val IS_BLOCKED = booleanPreferencesKey("isBlocked")
        private val SHOW_REPORT = booleanPreferencesKey("showReport")
        private val AUTO_DETECT = booleanPreferencesKey("auto_detect")
        private val GRADE_ID = intPreferencesKey("grade_id")
        private val FOS_ID = intPreferencesKey("fos_id")
    }

    val getUserToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN] ?: ""
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

    val showReport: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SHOW_REPORT] ?: true
    }

    val isAutoDetect: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_DETECT] ?: true
    }


    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = username
        }
    }

    suspend fun saveUserID(userID: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userID
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

    suspend fun setGradeId(gradeId: Int) {
        context.dataStore.edit { preferences ->
            preferences[GRADE_ID] = gradeId
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
}