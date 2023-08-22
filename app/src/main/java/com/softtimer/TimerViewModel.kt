package com.softtimer

import android.app.Application
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.softtimer.repository.DataStoreRepository
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.MID_ANIMATION_DURATION
import com.softtimer.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "TimerViewModel"

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DataStoreRepository(application)

    suspend fun <T> readFromDataStore(key: Preferences.Key<T>): Flow<T?> {
        return repository.readFromDataStore(key)
    }

    fun <T> saveToDataStore(key: Preferences.Key<T>, value: T) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveToDataStore(key, value)
        }
}
