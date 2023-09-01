package com.softtimer.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.softtimer.R
import com.softtimer.TimerViewModel
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.ButtonTextLight
import com.softtimer.ui.theme.Blue
import com.softtimer.ui.theme.BlueFlash
import com.softtimer.ui.theme.BlueLight
import com.softtimer.ui.theme.ButtonTextDark
import com.softtimer.ui.theme.FaintShadow1Dark
import com.softtimer.ui.theme.FaintShadow1Light
import com.softtimer.ui.theme.Orbitron
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.ui.theme.LightBlue
import com.softtimer.ui.theme.LightOrange
import com.softtimer.ui.theme.MID_ANIMATION_DURATION
import com.softtimer.ui.theme.MidBlue
import com.softtimer.ui.theme.MidOrange
import com.softtimer.ui.theme.Orange
import com.softtimer.ui.theme.OrangeFlash
import com.softtimer.ui.theme.OrangeLight
import com.softtimer.util.Constants.CLOCK_MAX_SIZE
import com.softtimer.util.Constants.CLOCK_MIN_SIZE
import com.softtimer.util.absPad
import com.softtimer.util.arcShadow
import com.softtimer.util.calculateShadowXOffset

private const val TAG = "Clock1"

@Composable
fun Clock(
    modifier: Modifier = Modifier,
    clockSize: Float,
    timerService: TimerService,
    viewModel: TimerViewModel,
    isDarkTheme: Boolean,
    onClockSizeChanged: (Float) -> Unit,
    onClockAnimationStateChanged: (Boolean) -> Unit,
) {
    val timerState = timerService.timerState

    val clockSizeModifier by animateFloatAsState(
        targetValue = clockSize,
        animationSpec = tween(
            durationMillis = MID_ANIMATION_DURATION,
            easing = LinearEasing
        )
    )

    LaunchedEffect(key1 = timerState) {
        when (timerState) {
            TimerState.Idle -> {
                onClockSizeChanged(CLOCK_MIN_SIZE)
                viewModel.clockInitialStart = true
                animate(
                    initialValue = viewModel.progressBarSweepAngle,
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = MID_ANIMATION_DURATION,
                        easing = LinearEasing
                    )
                ) { value, _ ->
                    viewModel.progressBarSweepAngle = value
                }
                onClockAnimationStateChanged(false)
            }

            TimerState.Running -> {
                if (viewModel.clockInitialStart) {
                    onClockSizeChanged(CLOCK_MAX_SIZE)
                    viewModel.clockInitialStart = false
                    viewModel.showOvertime = false

                    onClockAnimationStateChanged(true)

                    //progress bar animation that started when timer does
                    animate(
                        initialValue = viewModel.progressBarSweepAngle,
                        targetValue = 360f,
                        animationSpec = tween(
                            durationMillis = MID_ANIMATION_DURATION,
                            easing = LinearEasing
                        )
                    ) { value, _ ->
                        viewModel.progressBarSweepAngle = value
                    }

                    onClockAnimationStateChanged(false)
                }
                //progress bar animation that running with timer
                animate(
                    initialValue = viewModel.progressBarSweepAngle,
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = timerService.duration.inWholeMilliseconds.toInt(),
                        easing = LinearEasing
                    )
                ) { value, _ ->
                    viewModel.progressBarSweepAngle = value
                }
            }

            TimerState.Paused -> {
                viewModel.progressBarSweepAngleTarget = viewModel.progressBarSweepAngle
            }

            TimerState.Ringing -> {
                viewModel.showOvertime = true
                viewModel.progressBarSweepAngleTarget = 0f
                viewModel.progressBarSweepAngleTarget = 360f
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
            painter = painterResource(id = if(isDarkTheme) R.drawable.dark_bottom_circle else R.drawable.bottom_circle),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        ProgressBar(
            isDarkTheme = isDarkTheme,
            sweepAngle = viewModel.progressBarSweepAngle,
            diameter = 210f * clockSizeModifier,//210
            sizeModifier = clockSizeModifier
        )

        Image(
            modifier = Modifier
                .size(200.dp * clockSizeModifier)
                .offset(x = 5.dp * clockSizeModifier, y = 12.dp * clockSizeModifier),
            painter = painterResource(id = if(isDarkTheme) R.drawable.dark_mid_circle_group else R.drawable.mid_circle_group),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Indicator(
            modifier = Modifier.offset(y = (-76f * clockSizeModifier).dp),
            isDarkTheme = isDarkTheme,
            sweepAngle = viewModel.progressBarSweepAngle,
            sizeModifier = clockSizeModifier
        )

        Image(
            modifier = Modifier
                .size(160.dp * clockSizeModifier)
                .offset(x = 10.dp * clockSizeModifier, y = 10.dp * clockSizeModifier),
            painter = painterResource(id = if(isDarkTheme) R.drawable.dark_top_circle else R.drawable.top_circle),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        if (viewModel.showOvertime) {
            TimerNumbers(
                timerState = timerService.timerState,
                isDarkTheme = isDarkTheme,
                hours = timerService.hState.absPad(),
                minutes = timerService.minState.absPad(),
                seconds = timerService.sState.absPad(),
                showOvertime = viewModel.showOvertime,
                overtimeMins = timerService.overtimeMins.absPad(),
                overtimeSecs = timerService.overtimeSecs.absPad(),
                overtimeMillis = timerService.getOvertimeMillis(),
                sizeModifier = clockSizeModifier
            )
        } else {
            TimerNumbers(
                timerState = timerService.timerState,
                isDarkTheme = isDarkTheme,
                hours = timerService.hState.absPad(),
                minutes = timerService.minState.absPad(),
                seconds = timerService.sState.absPad(),
                showOvertime = viewModel.showOvertime,
                sizeModifier = clockSizeModifier
            )
        }
    }
}

@Composable
fun TimerNumbers(
    timerState: TimerState,
    isDarkTheme: Boolean,
    hours: String,
    minutes: String,
    seconds: String,
    showOvertime: Boolean = false,
    overtimeMins: String = "00",
    overtimeSecs: String = "00",
    overtimeMillis: String = "00",
    sizeModifier: Float
) {
    val isHourVisible = hours != "00"

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
                    if (isHourVisible) append("${hours}:")
                    append(minutes)
                    append(":")
                    append(seconds)
                }
            },
            fontFamily = Orbitron,
            color = if (isDarkTheme) FaintShadow1Dark else FaintShadow1Light,
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
                    if (isHourVisible) append("${hours}:")
                    append(minutes)
                    append(":")
                    append(seconds)
                }
            },
            fontFamily = Orbitron,
            color = if (isDarkTheme) ButtonTextDark else ButtonTextLight,
            fontSize = if (isHourVisible) (16f * sizeModifier).sp else (20f * sizeModifier).sp//16//20
        )

        if (showOvertime) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 23.dp),
                text = "-$overtimeMins:$overtimeSecs.$overtimeMillis",
                fontFamily = Orbitron,
                color = if (isDarkTheme) Orange else Blue,
                fontSize = (10f * sizeModifier).sp
            )
        }
    }
}

@Composable
fun ProgressBar(
    sweepAngle: Float,
    isDarkTheme: Boolean,
    diameter: Float,
    sizeModifier: Float
) {
    Canvas(
        modifier = Modifier
            .size(diameter.dp)
            .arcShadow(
                color = if (isDarkTheme) OrangeLight else BlueLight,
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
                        colors = if (isDarkTheme) {
                            listOf(
                                LightOrange,
                                Orange,
                                Orange,
                            )
                        } else {
                            listOf(
                                LightBlue,
                                Blue,
                                Blue,
                            )
                        },
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
                        colors = if (isDarkTheme) listOf(
                            Color.Transparent,
                            Color.Transparent,
                            OrangeFlash,
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
                        )
                        else
                            listOf(
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
                        colors = if (isDarkTheme)
                            listOf(
                                Orange,
                                Orange,
                                MidOrange,
                                MidOrange,
                                LightOrange
                            )
                        else
                            listOf(
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
fun Indicator(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    sweepAngle: Float,
    sizeModifier: Float
) {
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
            painter = painterResource(id = if(isDarkTheme) R.drawable.dark_indicator_shadow else R.drawable.indicator_shadow),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )

        //Indicator
        Box(
            modifier = modifier
                .background(if(isDarkTheme) Orange else Blue)
                .size(
                    width = (4 * sizeModifier).dp,//4
                    height = (58 * sizeModifier).dp//58
                )
        )
    }
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
