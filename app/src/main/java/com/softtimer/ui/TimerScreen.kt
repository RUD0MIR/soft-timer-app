package com.softtimer.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.softtimer.R
import com.softtimer.TimerViewModel
import com.softtimer.service.ServiceHelper
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.DarkGray
import com.softtimer.ui.theme.LightGray
import com.softtimer.ui.theme.MID_ANIMATION_DURATION
import com.softtimer.ui.theme.SHORT_ANIMATION_DURATION
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.ui.theme.White
import com.softtimer.util.Constants.ACTION_SERVICE_RESET
import com.softtimer.util.Constants.ACTION_SERVICE_START
import com.softtimer.util.Constants.ACTION_SERVICE_STOP

private const val TAG = "TimerScreen"

@Composable
fun TimerScreen(
    timerService: TimerService,
    viewModel: TimerViewModel,
) {
    var clockStartResetAnimationRunning by rememberSaveable { mutableStateOf(false) }
    var isDarkTheme = viewModel.isDarkTheme.collectAsState(false).value == true

    val timerState = timerService.timerState
    val context = LocalContext.current

    LaunchedEffect(key1 = timerState) {
        if (timerState == TimerState.Idle || timerState == TimerState.Reset) {
            timerService.hState = viewModel.hPickerState
            timerService.minState = viewModel.minPickerState
            timerService.sState = viewModel.sPickerState
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colorStops = if (isDarkTheme)
                        arrayOf(
                            Pair(0.3f, Black),
                            Pair(1f, DarkGray)
                        )
                    else
                        arrayOf(
                            Pair(0.3f, White),
                            Pair(1f, LightGray)
                        )
                )
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

            Clock(
                modifier = Modifier.constrainAs(clock) {
                    top.linkTo(topGuideLine)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                isDarkTheme = isDarkTheme,
                timerState = timerState,
                duration = timerService.duration,
                hState = timerService.hState,
                minState = timerService.minState,
                sState = timerService.sState,
                overtimeMins = timerService.overtimeMins,
                overtimeSecs = timerService.overtimeSecs,
                overtimeMillis = timerService.getOvertimeMillis(),
                clockSize = viewModel.clockSize,
                clockInitialStart = viewModel.clockInitialStart,
                progressBarSweepAngle = viewModel.progressBarSweepAngle,
                showOvertime = viewModel.showOvertime,
                onClockSizeChanged = {  viewModel.clockSize = it },
                onClockInitialStartChange = { viewModel.clockInitialStart = it },
                onProgressBarSweepAngleChange = { viewModel.progressBarSweepAngle = it },
                onShowOvertimeChange = { viewModel.showOvertime = it },
                onProgressBarSweepAngleTargetChange = { viewModel.progressBarSweepAngleTarget = it },
                onClockAnimationStateChanged = { clockStartResetAnimationRunning = it}
            )

            PickerSection(
                modifier = Modifier.constrainAs(picker) {
                    top.linkTo(clock.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(bottomGuideLine, margin = 18.dp)
                },
                isDarkTheme = isDarkTheme,
                timerState = timerService.timerState,
                pickerVisibilityValue = viewModel.pickerVisibilityValue,
                onPickerVisibilityValueChanged = { viewModel.pickerVisibilityValue = it },
                isVisible = viewModel.isVisible,
                onVisibilityChanged = { viewModel.isVisible = it },
                hValue = viewModel.hPickerState,
                minValue = viewModel.minPickerState,
                sValue = viewModel.sPickerState,
                onHPickerStateChanged = { selectedHour ->
                    timerService.hState = selectedHour
                    viewModel.hPickerState = selectedHour
                },
                onMinPickerStateChanged = { selectedMin ->
                    timerService.minState = selectedMin
                    viewModel.minPickerState = selectedMin
                },
                onSecPickerStateChanged = { selectedSec ->
                    timerService.sState = selectedSec
                    viewModel.sPickerState = selectedSec
                }
            )
        }

        ActionsSection(
            modifier = Modifier.align(Alignment.BottomCenter),
            timerState = timerState,
            isDarkTheme = isDarkTheme,
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
                    timerService.secondReset = true

                } else if (timerState == TimerState.Idle) {
                    viewModel.hPickerState = 0
                    viewModel.minPickerState = 0
                    viewModel.sPickerState = 0

                    timerService.apply {
                        hState = 0
                        minState = 0
                        sState = 0
                        showOvertime = false
                    }
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
                if(isDarkTheme) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                isDarkTheme = !isDarkTheme
            }
        )
    }
}

@Composable
fun ActionsSection(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    isDarkTheme: Boolean,
    onButtonResetClick: () -> Unit,
    onButtonPlayPauseClick: () -> Unit,
    onThemeButtonClick: () -> Unit,
) {
    Row(
        modifier.fillMaxWidth(0.9f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //restart button
        RestartButton(
            size = if (timerState == TimerState.Ringing) 90.dp else 50.dp,
            isDarkTheme = isDarkTheme
        ) {
            onButtonResetClick()
        }

        if (timerState != TimerState.Ringing) {
            PlayPauseButton(
                size = 90.dp,
                timerState = timerState,
                isDarkTheme = isDarkTheme
            ) {
                onButtonPlayPauseClick()
            }

            ThemeButton(size = 50.dp, isDarkTheme = isDarkTheme) {
                onThemeButtonClick()
            }
        }
    }
}

@Composable
fun RestartButton(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    size: Dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val restartAnimation by rememberLottieComposition(
        if (isDarkTheme) LottieCompositionSpec.RawRes(R.raw.dark_anim_reset)
        else LottieCompositionSpec.RawRes(R.raw.anim_reset)
    )
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(1f) }

    LaunchedEffect(key1 = isPlaying) {
        if (isPlaying) {
            var targetValue = 1f

            animate(
                initialValue = progress,
                targetValue = targetValue,
                animationSpec = tween(
                    durationMillis = MID_ANIMATION_DURATION,
                    easing = LinearEasing
                )
            ) { value, _ ->
                progress = value
            }

            targetValue = 0.5f

            animate(
                initialValue = 0f,
                targetValue = targetValue,
                animationSpec = tween(
                    durationMillis = MID_ANIMATION_DURATION,
                    easing = LinearEasing
                )
            ) { value, _ ->
                progress = value
                if (value == targetValue) isPlaying = false
            }
        } else {
            progress = 0.5f
        }
    }

    Box(
        modifier = modifier
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick()
                isPlaying = true
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(size),
            painter = painterResource(id = if (isDarkTheme) R.drawable.dark_button else R.drawable.button),
            contentDescription = null
        )

        LottieAnimation(
            modifier = Modifier
                .size(size * 0.3f)
                .offset(x = (-1).dp, y = (-0.3).dp),
            composition = restartAnimation,
            progress = { progress }
        )
    }
}

@Composable
fun PlayPauseButton(
    size: Dp,
    isDarkTheme: Boolean,
    timerState: TimerState,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isAnimPlaying by remember { mutableStateOf(false) }
    val pauseAnimComposition by rememberLottieComposition(
        if (isDarkTheme) LottieCompositionSpec.RawRes(R.raw.dark_anim_pause)
        else LottieCompositionSpec.RawRes(R.raw.anim_pause)
    )
    var progress by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(key1 = isAnimPlaying, key2 = timerState) {
        if (timerState == TimerState.Running) {
            //from pause icon to play icon animation
            val targetValue = 1f
            animate(
                initialValue = progress,
                targetValue = targetValue,
                animationSpec = tween(
                    durationMillis = SHORT_ANIMATION_DURATION,
                    easing = LinearEasing
                )
            ) { value, _ ->
                progress = value
                if (value == targetValue) isAnimPlaying = false
            }
        } else if (timerState == TimerState.Idle || timerState == TimerState.Paused) {
            //from play icon to pause icon animation
            val targetValue = 0f
            animate(
                initialValue = progress,
                targetValue = targetValue,
                animationSpec = tween(
                    durationMillis = SHORT_ANIMATION_DURATION,
                    easing = LinearEasing
                )
            ) { value, _ ->
                progress = value
                if (value == targetValue) isAnimPlaying = false
            }
        }
    }

    Box(
        modifier = Modifier
            .clickable(interactionSource = interactionSource, indication = null) {
                if (!isAnimPlaying && timerState != TimerState.Reset) {
                    isAnimPlaying = true
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(size),
            painter = painterResource(id = if (isDarkTheme) R.drawable.dark_button else R.drawable.button),
            contentDescription = null
        )

        LottieAnimation(
            modifier = Modifier
                .size(20.dp)
                .offset(x = (-1).dp, y = (-0.3).dp),
            composition = pauseAnimComposition,
            progress = { progress }
        )
    }
}

@Composable
fun ThemeButton(
    size: Dp,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isLightTheme by remember { mutableStateOf(true) }
    var isAnimPlaying by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(
        if (isDarkTheme) LottieCompositionSpec.RawRes(R.raw.dark_anim_moon_sun)
        else LottieCompositionSpec.RawRes(R.raw.anim_moon_sun)
    )

    var progress by remember {
        mutableStateOf(0.4f)
    }

    LaunchedEffect(key1 = isAnimPlaying) {
        if (isAnimPlaying) {
            if (isLightTheme) {
                val targetValue = 0.5f
                progress = 0f

                animate(
                    initialValue = progress,
                    targetValue = targetValue,
                    animationSpec = tween(
                        durationMillis = MID_ANIMATION_DURATION,
                        easing = LinearEasing
                    )
                ) { value, _ ->
                    progress = value
                    if (value == targetValue) isAnimPlaying = false
                }

            } else {
                val targetValue = 1f
                progress = 0.5f

                animate(
                    initialValue = progress,
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = MID_ANIMATION_DURATION,
                        easing = LinearEasing
                    )
                ) { value, _ ->
                    progress = value
                    if (value == targetValue) isAnimPlaying = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .clickable(interactionSource = interactionSource, indication = null) {
                if (!isAnimPlaying) {
                    onClick()
                    isAnimPlaying = true
                    isLightTheme = !isLightTheme
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(size),
            painter = painterResource(id = if (isDarkTheme) R.drawable.dark_button else R.drawable.button),
            contentDescription = null
        )

        LottieAnimation(
            modifier = Modifier
                .size(48.dp)
                .offset(x = (-1).dp, y = (-0.3).dp),
            composition = composition,
            progress = { progress }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    SoftTImerTheme {
    }
}