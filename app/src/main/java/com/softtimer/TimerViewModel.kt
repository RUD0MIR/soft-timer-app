package com.softtimer

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.softtimer.repository.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val TAG = "TimerViewModel"

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    //variables for Clock.kt
    var lastSetTime by mutableStateOf(0L)
    var clockInitialStart by mutableStateOf(true)
    var progressBarSweepAngleTarget by mutableStateOf(360f)
    var progressBarSweepAngle by mutableStateOf(0f)
    var clockSizeModifier by mutableStateOf(1.1f)

    //variables for PickerSection.kt
    var pickerVisibilityValue by mutableStateOf(1f)
    var isVisible by mutableStateOf(true)

    //TODO variables for NumberPicker.kt
    var hPickerState by mutableStateOf(0)
    var minPickerState by mutableStateOf(0)
    var sPickerState by mutableStateOf(0)

    var secondReset by mutableStateOf(false)


    private val repository = DataStoreRepository(application)

    suspend fun <T> readFromDataStore(key: Preferences.Key<T>): Flow<T?> {
            return repository.readFromDataStore(key)
    }

    fun <T>saveToDataStore(key: Preferences.Key<T>, value: T) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveToDataStore(key, value)
    }
}
