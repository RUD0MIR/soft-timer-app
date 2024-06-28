package com.softtimer

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.softtimer.repository.DataStoreRepository
import com.softtimer.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "TimerViewModel"

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private var _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    var hPickerState by mutableIntStateOf(0)
    var minPickerState by mutableIntStateOf(0)
    var sPickerState by mutableIntStateOf(0)

    private val repository = DataStoreRepository(application)

    private fun isDarkTheme(): Flow<Boolean?> {
        return repository.isDarkTheme()
    }

    fun saveThemeStateToDataStore() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveThemeState(_isDarkTheme.value)
        }

    fun changeSystemTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setSystemThemeFromDataStore() {
        _isDarkTheme.value = repository.isDarkTheme()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                false
            ).value ?: false
    }

    fun resetTimerState() {
        hPickerState = 0
        minPickerState = 0
        sPickerState = 0
    }
}
