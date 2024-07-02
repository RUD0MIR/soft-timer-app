package com.softtimer.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softtimer.service.ServiceHelper
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.Constants.ACTION_SERVICE_RESET
import com.softtimer.util.Constants.ACTION_SERVICE_START
import com.softtimer.util.Constants.ACTION_SERVICE_STOP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

private const val TAG = "TimerScreen"

@Composable
fun TimerScreen(
    timerState: TimerState,
    duration: Duration,

    hState: Int,
    minState: Int,
    sState: Int,
    overtimeMins: Int,
    overtimeSecs: Int,
    overtimeMillis: String,
    hPickerState: Int,
    minPickerState: Int,
    sPickerState: Int,

    onTimerServiceHStateChange: (Int) -> Unit,
    onTimerServiceMinStateChange: (Int) -> Unit,
    onTimerServiceSStateChange: (Int) -> Unit,
    onHPickerStateChanged: (Int) -> Unit,
    onMinPickerStateChanged: (Int) -> Unit,
    onSecPickerStateChanged: (Int) -> Unit,
    onSecondReset: (Boolean) -> Unit,
    onStateReset: () -> Unit,
    isDarkTheme: StateFlow<Boolean>,
    onSystemThemeChange: () -> Unit,
) {
    var clockStartResetAnimationRunning by rememberSaveable { mutableStateOf(false) }
    val isDarkThemeValue = isDarkTheme.collectAsStateWithLifecycle(false).value
    val context = LocalContext.current

    LaunchedEffect(key1 = timerState) {
        if (timerState == TimerState.Idle || timerState == TimerState.Reset) {
            onTimerServiceHStateChange(hPickerState)
            onTimerServiceMinStateChange(minPickerState)
            onTimerServiceSStateChange(sPickerState)
        }
    }

    val mainSurfaceGradient = Brush.linearGradient(
        colorStops =
        arrayOf(
            Pair(0.3f, MaterialTheme.colorScheme.surface),
            Pair(1f, MaterialTheme.colorScheme.surfaceVariant)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = mainSurfaceGradient
            )
            .padding(bottom = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (clock, picker) = createRefs()
            val bottomGuideLine = createGuidelineFromBottom(fraction = 0.12f)
            val topGuideLine = createGuidelineFromTop(fraction = 0.16f)

            var clockSizeModifier by rememberSaveable {
                mutableFloatStateOf(1f)
            }
            var clockInitialStart by rememberSaveable {
                mutableStateOf(true)
            }
            var progressBarSweepAngle by rememberSaveable {
                mutableFloatStateOf(0f)
            }
            var showOvertime by rememberSaveable {
                mutableStateOf(false)
            }

            Clock(
                modifier = Modifier.constrainAs(clock) {
                    top.linkTo(topGuideLine)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                timerState = timerState,
                duration = duration,
                hState = hState,
                minState = minState,
                sState = sState,
                overtimeMins = overtimeMins,
                overtimeSecs = overtimeSecs,
                overtimeMillis = overtimeMillis,
                onClockStartResetAnimationStateChanged = { clockStartResetAnimationRunning = it },
                clockSizeModifier = clockSizeModifier,
                clockInitialStart = clockInitialStart,
                progressBarSweepAngle = progressBarSweepAngle,
                showOvertime = showOvertime,
                onClockSizeModifierChange = { clockSizeModifier = it },
                onClockInitialStartChange = { clockInitialStart = it },
                onProgressBarSweepAngleChange = { progressBarSweepAngle = it },
                onShowOvertimeChange = { showOvertime = it }
            )

            PickerSection(
                modifier = Modifier.constrainAs(picker) {
                    top.linkTo(clock.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(bottomGuideLine, margin = 18.dp)
                },
                isDarkTheme = isDarkThemeValue,
                timerState = timerState,
                hValue = hPickerState,
                minValue = minPickerState,
                sValue = sPickerState,
                onHPickerStateChanged = onHPickerStateChanged,
                onMinPickerStateChanged = onMinPickerStateChanged,
                onSecPickerStateChanged = onSecPickerStateChanged
            )
        }

        ButtonSection(
            modifier = Modifier.align(Alignment.BottomCenter),
            timerState = timerState,
            isDarkTheme = isDarkThemeValue,
            onButtonResetClick = {
                if (
                    timerState == TimerState.Running ||
                    timerState == TimerState.Paused ||
                    timerState == TimerState.Ringing &&
                    !clockStartResetAnimationRunning
                ) {
                    ServiceHelper.triggerForegroundService(
                        context = context,
                        action = ACTION_SERVICE_RESET
                    )
                    onSecondReset(true)

                } else if (timerState == TimerState.Idle) {
                    onStateReset()
                }
            },
            onButtonPlayPauseClick = {
                if (!clockStartResetAnimationRunning) {
                    ServiceHelper.triggerForegroundService(
                        context = context,
                        action = if (timerState == TimerState.Running) ACTION_SERVICE_STOP
                        else ACTION_SERVICE_START
                    )
                }
            },
            onThemeButtonClick = {
                if (isDarkThemeValue) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                onSystemThemeChange()
            }
        )
    }
}




@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    SoftTImerTheme(dynamicColor = false) {
        TimerScreen(
            timerState = TimerState.Reset,
            duration = Duration.ZERO,
            hState = 0,
            minState = 0,
            sState = 0,
            overtimeMins = 0,
            overtimeSecs = 0,
            overtimeMillis = "",
            hPickerState = 0,
            minPickerState = 0,
            sPickerState = 0,
            onTimerServiceHStateChange = {},
            onTimerServiceMinStateChange = {},
            onTimerServiceSStateChange = {},
            onHPickerStateChanged = {},
            onMinPickerStateChanged = {},
            onSecPickerStateChanged = {},
            onSecondReset = {},
            onStateReset = {},
            isDarkTheme = MutableStateFlow(false),
            onSystemThemeChange = {}

        )
    }
}