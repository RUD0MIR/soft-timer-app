package com.softtimer

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
    var isDarkTheme by mutableStateOf(
        isSystemInDarkTheme(application.applicationContext)
    )

    var hPickerState by mutableStateOf(0)
    var minPickerState by mutableStateOf(0)
    var sPickerState by mutableStateOf(0)

    var pickerVisibilityValue by mutableStateOf(1f)
    var isVisible by mutableStateOf(true)

    var clockInitialStart by mutableStateOf(true)
    var progressBarSweepAngleTarget by mutableStateOf(360f)
    var progressBarSweepAngle by mutableStateOf(0f)
    var showOvertime by mutableStateOf(false)
    var clockSize by mutableStateOf(Constants.CLOCK_MIN_SIZE)

    private fun isSystemInDarkTheme(context: Context): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_YES -> true
            UiModeManager.MODE_NIGHT_NO -> false
            else -> false
        }
    }

    private val repository = DataStoreRepository(application)

    suspend fun <T> readFromDataStore(key: Preferences.Key<T>): Flow<T?> {
        return repository.readFromDataStore(key)
    }

    fun <T> saveToDataStore(key: Preferences.Key<T>, value: T) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveToDataStore(key, value)
        }
}
