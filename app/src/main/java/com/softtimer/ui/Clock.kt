package com.softtimer.ui

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.softtimer.R
import com.softtimer.TimerViewModel
import com.softtimer.repository.DataStoreKeys
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.timerService
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.Blue
import com.softtimer.ui.theme.BlueFlash
import com.softtimer.ui.theme.BlueLight
import com.softtimer.ui.theme.FaintShadow1
import com.softtimer.ui.theme.Orbitron
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.ui.theme.LightBlue
import com.softtimer.ui.theme.MID_ANIMATION_DURATION
import com.softtimer.ui.theme.MidBlue
import com.softtimer.util.Constants
import com.softtimer.util.Constants.CLOCK_MAX_SIZE
import com.softtimer.util.Constants.CLOCK_MIN_SIZE
import com.softtimer.util.absPad
import com.softtimer.util.arcShadow
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val TAG = "Clock1"

@Composable
fun Clock(
    modifier: Modifier = Modifier,
    clockSize: Float,
    onClockSizeChanged: (Float) -> Unit,
    onClockAnimationStateChanged: (Boolean) -> Unit,
    onClockInitialStart: () -> Unit
) {
    var clockInitialStart by rememberSaveable { mutableStateOf(true) }
    var progressBarSweepAngleTarget by rememberSaveable { mutableStateOf(360f) }
    var progressBarSweepAngle by rememberSaveable { mutableStateOf(0f) }
    var showOvertime by rememberSaveable{ mutableStateOf(false) }

    val clockSizeModifier by animateFloatAsState(
        targetValue = clockSize,
        animationSpec = tween(
            durationMillis = MID_ANIMATION_DURATION,
            easing = LinearEasing
        )
    )

    val timerState = timerService.timerState

    LaunchedEffect(key1 = timerState) {
        when (timerState) {
            TimerState.Idle -> {
                onClockSizeChanged(CLOCK_MIN_SIZE)
                clockInitialStart = true
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
                onClockAnimationStateChanged(false)
            }

            TimerState.Running -> {
                if (clockInitialStart) {
                    onClockSizeChanged(CLOCK_MAX_SIZE)
                    clockInitialStart = false
                    showOvertime = false

                    onClockInitialStart()

                    onClockAnimationStateChanged(true)

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

                    onClockAnimationStateChanged(false)
                }
                //progress bar animation that running with timer
                animate(
                    initialValue = progressBarSweepAngle,
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = com.softtimer.timerService.duration.inWholeMilliseconds.toInt(),
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
                showOvertime = true
                progressBarSweepAngleTarget = 0f
                progressBarSweepAngleTarget = 360f

            }

            else -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp * clockSizeModifier),
        contentAlignment = Alignment.Center
    ) {

        Image(
            modifier = Modifier
                .size(284.dp * clockSizeModifier)
                .offset(y = 5.dp * clockSizeModifier),
            painter = painterResource(id = R.drawable.bottom_circle),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        ProgressBar(
            sweepAngle = progressBarSweepAngle,
            diameter = 210f * clockSizeModifier,//210
            sizeModifier = clockSizeModifier
        )

        Image(
            modifier = Modifier
                .size(200.dp * clockSizeModifier)
                .offset(x = 5.dp * clockSizeModifier, y = 12.dp * clockSizeModifier),
            painter = painterResource(id = R.drawable.mid_circle_group),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Indicator(
            modifier = Modifier.offset(y = (-76f * clockSizeModifier).dp),
            sweepAngle = progressBarSweepAngle,
            sizeModifier = clockSizeModifier
        )

        Image(
            modifier = Modifier
                .size(160.dp * clockSizeModifier)
                .offset(x = 10.dp * clockSizeModifier, y = 10.dp * clockSizeModifier),
            painter = painterResource(id = R.drawable.top_circle),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        TimerNumbers(
            timerState = timerService.timerState,
            hours = timerService.hState,
            minutes = timerService.minState,
            seconds = timerService.sState,
            showOvertime = showOvertime,
            sizeModifier = clockSizeModifier
        )
    }
}

@Composable
fun TimerNumbers(
    timerState: TimerState,
    hours: Int,
    minutes: Int,
    seconds: Int,
    showOvertime: Boolean = false,
    overtimeMins: Int = 0,
    overtimeSecs: Int = 0,
    overtimeMillis: Int = 0,
    sizeModifier: Float
) {
    val isHourVisible = hours != 0
    val padHours = hours.absPad()
    val padMinutes = minutes.absPad()
    val padSeconds = seconds.absPad()

    Box(
        modifier = Modifier
            .size(width = 100.dp * sizeModifier, height = 60.dp * sizeModifier),
        contentAlignment = Alignment.Center
    ) {
        //Numbers shadow
        Text(
            modifier = Modifier
                .offset(y = (4f * sizeModifier).dp, x = (1f * sizeModifier).dp),//3
            text = if (timerState == TimerState.Ringing) {
                "00:00"
            } else {
                buildString {
                    if (isHourVisible) append("${padHours}:")
                    append(padMinutes)
                    append(":")
                    append(padSeconds)
                }
            },
            fontFamily = Orbitron,
            color = FaintShadow1,
            fontSize = if (isHourVisible)
                (16f * sizeModifier).sp
            else (20f * sizeModifier).sp//16//20
        )

        //Clock numbers
        Text(
            text = if (timerState == TimerState.Ringing) {
                "00:00"
            } else {
                buildString {
                    if (isHourVisible) append("${padHours}:")
                    append(padMinutes)
                    append(":")
                    append(padSeconds)
                }
            },
            fontFamily = Orbitron,
            color = Black,
            fontSize = if (isHourVisible) (16f * sizeModifier).sp else (20f * sizeModifier).sp//16//20
        )

        if (showOvertime) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 23.dp),
                text = "-$overtimeMins:$overtimeSecs.$overtimeMillis",
                fontFamily = Orbitron,
                color = Blue,
                fontSize = (10f * sizeModifier).sp
            )
        }
    }
}

@Composable
fun ProgressBar(sweepAngle: Float, diameter: Float, sizeModifier: Float) {
    Canvas(
        modifier = Modifier
            .size(diameter.dp)
            .arcShadow(
                color = BlueLight,
                startAngle = 0f,
                useCenter = true,
                spread = (2f * sizeModifier).dp,//2
                sweepAngle = sweepAngle,
                blurRadius = (4f * sizeModifier).dp,//4
                rotateAngle = 270f
            ),
        onDraw = {
            withTransform(
                { rotate(degrees = 270f, pivot = center) }
            ) {
                //progress bar itself
                drawArc(
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    size = Size(width = diameter.dp.toPx(), height = diameter.dp.toPx()),
                    useCenter = true,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            LightBlue,
                            Blue,
                            Blue,
                        ),
                        center = center
                    )
                )

                //flash effect
                drawArc(
                    startAngle = 10f,
                    sweepAngle = if (sweepAngle <= 30f) sweepAngle else 30f,
                    size = Size(width = diameter.dp.toPx(), height = diameter.dp.toPx()),
                    useCenter = true,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            BlueFlash,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                        ),
                    ),
                )

                //line around progress bar
                drawArc(
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    style = Stroke(width = (2 * sizeModifier).dp.toPx()),//2
                    size = Size(width = diameter.dp.toPx(), height = diameter.dp.toPx()),
                    useCenter = false,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Blue,
                            Blue,
                            MidBlue,
                            MidBlue,
                            LightBlue
                        )
                    )
                )
            }
        }
    )
}

@Composable
fun Indicator(modifier: Modifier = Modifier, sweepAngle: Float, sizeModifier: Float) {
    var shadowOffsetState by remember {
        mutableStateOf(0f)
    }

    val shadowOffset = animateFloatAsState(
        targetValue = shadowOffsetState,
        animationSpec = tween(easing = LinearEasing)
    )

    LaunchedEffect(key1 = sweepAngle) {
        shadowOffsetState = calculateShadowXOffset(
            sweepAngle = sweepAngle,
            maxOffset = 5f,//8
            minOffset = -5f,//-10
            lightSourceOffset = 345f
        )
    }

    Box(
        modifier = Modifier
            .rotate(sweepAngle)
            .size(width = 20.dp * sizeModifier, height = 70.dp * sizeModifier),//100.dp
        contentAlignment = Alignment.Center
    ) {
        //Indicator shadow
        Image(
            modifier = modifier
                .size(height = 60.dp * sizeModifier, width = 10.dp * sizeModifier)
                .align(Alignment.BottomEnd)
                .offset(x = (-5).dp)
                .offset(
                    x = shadowOffset.value.dp
                ),
            painter = painterResource(id = R.drawable.indicator_shadow),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )

        //Indicator
        Box(
            modifier = modifier
                .background(Blue)
                .size(
                    width = (4 * sizeModifier).dp,//4
                    height = (58 * sizeModifier).dp//58
                )
        )
    }
}

fun calculateShadowXOffset(
    sweepAngle: Float,
    minOffset: Float,
    maxOffset: Float,
    lightSourceOffset: Float
): Float {
    val totalCircle = 360f

    // Calculate the angle difference between the light source and the current sweep angle
    val angleDifference = sweepAngle - lightSourceOffset

    // Normalize the angle difference
    val normalizedAngle = (angleDifference + totalCircle) % totalCircle

    // Calculate the offset based on the normalized angle
    val offset = (10f * sin(Math.toRadians(normalizedAngle.toDouble()))).toFloat()

    // Apply the minimum and maximum offset
    var finalOffset: Float
    finalOffset = if (offset < minOffset) minOffset else offset
    finalOffset = if (offset > maxOffset) maxOffset else offset

    return finalOffset
}

@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    SoftTImerTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFD8D6D6)),
            contentAlignment = Alignment.Center
        ) {
            //Clock(timerService = TimerService())
        }
    }
}
