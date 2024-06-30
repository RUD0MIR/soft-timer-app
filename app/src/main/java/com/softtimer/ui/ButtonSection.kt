package com.softtimer.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.softtimer.R
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.Constants
import com.softtimer.util.circleShadow

@Composable
fun ButtonSection(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    isDarkTheme: Boolean,
    onButtonResetClick: () -> Unit,
    onButtonPlayPauseClick: () -> Unit,
    onThemeButtonClick: () -> Unit,
) {
    val buttonSize = 36.dp
    val bigButtonSize = 60.dp
    Row(
        modifier.fillMaxWidth(0.9f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //restart button
        RestartButton(
            size = if (timerState == TimerState.Ringing) bigButtonSize else buttonSize,
            isDarkTheme = isDarkTheme
        ) {
            onButtonResetClick()
        }

        if (timerState != TimerState.Ringing) {
            PlayPauseButton(
                size = bigButtonSize,
                timerState = timerState,
                isDarkTheme = isDarkTheme
            ) {
                onButtonPlayPauseClick()
            }

            ThemeButton(
                size = buttonSize,
                isDarkTheme = isDarkTheme
            ) {
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
    var progress by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(key1 = isPlaying) {
        if (isPlaying) {
            var targetValue = 1f

            animate(
                initialValue = progress,
                targetValue = targetValue,
                animationSpec = tween(
                    durationMillis = Constants.MID_ANIMATION_DURATION,
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
                    durationMillis = Constants.MID_ANIMATION_DURATION,
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
        ButtonCircle(size)

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
    modifier: Modifier = Modifier,
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
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(key1 = isAnimPlaying, key2 = timerState) {
        if (timerState == TimerState.Running) {
            //from pause icon to play icon animation
            val targetValue = 1f
            animate(
                initialValue = progress,
                targetValue = targetValue,
                animationSpec = tween(
                    durationMillis = Constants.SHORT_ANIMATION_DURATION,
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
                    durationMillis = Constants.SHORT_ANIMATION_DURATION,
                    easing = LinearEasing
                )
            ) { value, _ ->
                progress = value
                if (value == targetValue) isAnimPlaying = false
            }
        }
    }

    Box(
        modifier = modifier
            .clickable(interactionSource = interactionSource, indication = null) {
                if (!isAnimPlaying && timerState != TimerState.Reset) {
                    isAnimPlaying = true
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        ButtonCircle(size = size)

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
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isLightTheme by remember { mutableStateOf(!isDarkTheme) }
    var isAnimPlaying by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(
        if (isDarkTheme) LottieCompositionSpec.RawRes(R.raw.dark_anim_moon_sun)
        else LottieCompositionSpec.RawRes(R.raw.anim_moon_sun)
    )
    var progress by remember {
        mutableFloatStateOf(0.4f)
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
                        durationMillis = Constants.MID_ANIMATION_DURATION,
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
                        durationMillis = Constants.MID_ANIMATION_DURATION,
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
        modifier = modifier
            .clickable(interactionSource = interactionSource, indication = null) {
                if (!isAnimPlaying) {
                    onClick()
                    isAnimPlaying = true
                    isLightTheme = !isLightTheme
                }
            },
        contentAlignment = Alignment.Center
    ) {
        ButtonCircle(size = size)

        LottieAnimation(
            modifier = Modifier
                .size(48.dp)
                .offset(x = (-1).dp, y = (-0.3).dp),
            composition = composition,
            progress = { progress }
        )
    }
}

@Composable
fun ButtonCircle(size: Dp, modifier: Modifier = Modifier) {
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer
    Canvas(
        modifier = modifier
            .size(size)
            .circleShadow(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                radius = size - 2.dp,
                blurRadius = 10.dp,
                offsetX = 4.dp,
                offsetY = 4.dp
            )
            .circleShadow(
                color = MaterialTheme.colorScheme.surfaceBright,
                radius = size - 2.dp,
                blurRadius = 5.dp,
                offsetX = (-4).dp,
                offsetY = (-4).dp
            )
    ) {
        drawCircle(color = surfaceContainer)
    }
}

@Preview
@Composable
private fun ButtonSectionPreview() {
    SoftTImerTheme(dynamicColor = false) {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            ButtonSection(
                timerState = TimerState.Idle,
                isDarkTheme = false,
                onButtonResetClick = {  },
                onButtonPlayPauseClick = {  },
                onThemeButtonClick = {  }
            )
        }

    }
}