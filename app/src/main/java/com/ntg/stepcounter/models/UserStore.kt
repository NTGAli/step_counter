package com.ntg.stepcounter.models

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

    val getPhoneNumber: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PHONE_NUMBER] ?: ""
    }

    val getStatus: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[STATUS] ?: ""
    }

    val isVerified: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_VERIFIED] ?: false
    }

    val fieldStudy: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[FIELD_STUDY] ?: ""
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

    suspend fun isVerified(isVerified: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_VERIFIED] = isVerified
        }
    }
}