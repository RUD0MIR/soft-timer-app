package com.softtimer.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val PREFERENCE_NAME = "data_store"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

class DataStoreRepository(context: Context) {
    private val dataStore = context.dataStore

    suspend fun <T>saveToDataStore(key: Preferences.Key<T>, value: T){
        dataStore.edit { preference ->
            preference[key] = value
        }
    }

    suspend fun <T> readFromDataStore(key: Preferences.Key<T>): Flow<T?> {
       return dataStore.data
            .catch { exception ->
                if(exception is IOException){
                    Log.d("DataStore", exception.message.toString())
                    emit(emptyPreferences())
                }else {
                    throw exception
                }
            }
            .map { preference ->
                val value = preference[key]
                value
            }
    }
}

object DataStoreKeys {
    val LAST_HOUR = intPreferencesKey("last_hour")
    val LAST_MIN = intPreferencesKey("last_minute")
    val LAST_SEC = intPreferencesKey("last_second")
}