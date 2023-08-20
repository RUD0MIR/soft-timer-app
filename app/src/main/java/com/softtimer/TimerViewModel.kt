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
    //variables for Clock.kt
    var clockInitialStart by mutableStateOf(true)
    var progressBarSweepAngleTarget by mutableStateOf(360f)
    var progressBarSweepAngle by mutableStateOf(0f)
    var clockSizeModifier by mutableStateOf(1.1f)
    var clockStartResetAnimationRunning by mutableStateOf(false)

    //variables for PickerSection.kt
    var pickerVisibilityValue by mutableStateOf(1f)
    var isVisible by mutableStateOf(true)

    //variables for save last set time and show in NumberPicker when timer is reset
    var hPickerState by mutableStateOf(0)
    var minPickerState by mutableStateOf(0)
    var sPickerState by mutableStateOf(0)

    var secondReset by mutableStateOf(false)

    fun animateClockByTimerState(timerService: TimerService) {
        val timerState = timerService.timerState
        viewModelScope.launch {
            when (timerState) {
                TimerState.Idle -> {
                    clockSizeModifier = Constants.CLOCK_MIN_SIZE
                    clockInitialStart = true
                    clockStartResetAnimationRunning = true

                    timerService.apply {
                        hState = hPickerState
                        minState = minPickerState
                        sState = sPickerState
                    }
                    //progress bar animation that started when timer reset
                    animate(
                        initialValue = progressBarSweepAngle,
                        targetValue = 0f,
                        animationSpec = tween(
                            durationMillis = MID_ANIMATION_DURATION,
                            easing = LinearEasing
                        )
                    ) { value, _ ->
                        progressBarSweepAngle = value
                    }

                    clockStartResetAnimationRunning = false
                }

                TimerState.Running -> {
                    if (clockInitialStart) {
                        clockSizeModifier = Constants.CLOCK_MAX_SIZE
                        clockInitialStart = false

                        hPickerState = timerService.hState
                        minPickerState = timerService.minState
                        sPickerState = timerService.sState

                        clockStartResetAnimationRunning = true


                        //progress bar animation that started when timer does
                        animate(
                            initialValue = progressBarSweepAngle,
                            targetValue = 360f,
                            animationSpec = tween(
                                durationMillis = MID_ANIMATION_DURATION,
                                easing = LinearEasing
                            )
                        ) { value, _ ->
                            progressBarSweepAngle = value
                        }

                        clockStartResetAnimationRunning = false
                    }
                    //progress bar animation that running with timer
                    animate(
                        initialValue = progressBarSweepAngle,
                        targetValue = 0f,
                        animationSpec = tween(
                            durationMillis = timerService.duration.inWholeMilliseconds.toInt(),
                            easing = LinearEasing
                        )
                    ) { value, _ ->
                        progressBarSweepAngle = value
                    }
                }

                TimerState.Paused -> {
                    progressBarSweepAngleTarget = progressBarSweepAngle
                }

                TimerState.Ringing -> {
                    progressBarSweepAngleTarget = 0f
                    progressBarSweepAngleTarget = 360f

                }

                else -> {}
            }
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
