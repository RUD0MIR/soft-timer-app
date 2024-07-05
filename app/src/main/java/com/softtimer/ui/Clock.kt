package com.softtimer.ui

import android.content.res.Configuration
import android.icu.util.Calendar
import android.util.Log
import android.widget.ProgressBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softtimer.R
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.Orbitron
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.Constants.CLOCK_MAX_SIZE
import com.softtimer.util.Constants.CLOCK_MIN_SIZE
import com.softtimer.util.Constants.MID_ANIMATION_DURATION
import com.softtimer.util.absPad
import com.softtimer.util.arcShadow
import com.softtimer.util.calculateShadowXOffset
import com.softtimer.util.circleShadow
import com.softtimer.util.rectShadow
import java.util.Date
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "Clock1"

@Composable
fun Clock(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,

    timerState: TimerState,
    duration: Duration,
    hState: Int,
    minState: Int,
    sState: Int,
    overtimeMins: Int,
    overtimeSecs: Int,
    overtimeMillis: String,

    clockSize: Float,
    clockInitialStart: Boolean,
    progressBarSweepAngle: Float,
    showOvertime: Boolean,
    onClockSizeChange: (Float) -> Unit,
    onClockInitialStartChange: (Boolean) -> Unit,
    onProgressBarSweepAngleChange: (Float) -> Unit,
    onShowOvertimeChange: (Boolean) -> Unit,
    onClockStartResetAnimationStateChanged: (Boolean) -> Unit,
) {
    val clockSizeModifier by animateFloatAsState(
        targetValue = clockSize,
        animationSpec = tween(
            durationMillis = MID_ANIMATION_DURATION,
            easing = LinearEasing
        ), label = "clockSizeAnimation"
    )

    LaunchedEffect(key1 = timerState) {
        when (timerState) {
            TimerState.Idle -> {
                onClockInitialStartChange(true)
                onClockSizeChange(CLOCK_MIN_SIZE)
                onClockStartResetAnimationStateChanged(true)
                animate(
                    initialValue = progressBarSweepAngle,
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = MID_ANIMATION_DURATION,
                        easing = LinearEasing
                    )
                ) { value, _ ->
                    onProgressBarSweepAngleChange(value)
                }
                onClockStartResetAnimationStateChanged(false)
            }

            TimerState.Running -> {
                if (clockInitialStart) {
                    onClockSizeChange(CLOCK_MAX_SIZE)
                    onClockInitialStartChange(false)
                    onShowOvertimeChange(false)

                    onClockStartResetAnimationStateChanged(true)

                    //progress bar animation that started when timer does
                    animate(
                        initialValue = progressBarSweepAngle,
                        targetValue = 360f,
                        animationSpec = tween(
                            durationMillis = MID_ANIMATION_DURATION,
                            easing = LinearEasing
                        )
                    ) { value, _ ->
                        onProgressBarSweepAngleChange(value)
                    }

                    onClockStartResetAnimationStateChanged(false)
                }
                //progress bar animation that running with timer
                animate(
                    initialValue = progressBarSweepAngle,
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = duration.inWholeMilliseconds.toInt(),
                        easing = LinearEasing
                    )
                ) { value, _ ->
                    onProgressBarSweepAngleChange(value)
                }
            }

            TimerState.Ringing -> {
                onShowOvertimeChange(true)
                onProgressBarSweepAngleChange(0f)
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
        BottomCircle(270.dp * clockSizeModifier)

        ProgressBar(
            sweepAngle = progressBarSweepAngle,
            diameter = 210f * clockSizeModifier,//210
            sizeModifier = clockSizeModifier
        )

        MidCircleBackgroundGradient(248.dp * clockSizeModifier)
        MidCircle(circleRadius = 332.dp.value * clockSizeModifier)

        Indicator(
            modifier = Modifier.offset(y = (-76f * clockSizeModifier).dp),
            sweepAngle = progressBarSweepAngle,
            sizeModifier = clockSizeModifier
        )

        FrontCircle(size = 140.dp * clockSizeModifier)

        TimerNumbers(
            timerState = timerState,
            hours = hState.absPad(),
            minutes = minState.absPad(),
            seconds = sState.absPad(),
            showOvertime = showOvertime,
            overtimeMins = overtimeMins.absPad(),
            overtimeSecs = overtimeSecs.absPad(),
            overtimeMillis = overtimeMillis,
            sizeModifier = clockSizeModifier
        )
    }
}

@Composable
fun FrontCircle(size: Dp, modifier: Modifier = Modifier) {
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer
    Canvas(
        modifier = modifier
            .size(size)
            .circleShadow(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                radius = size,
                blurRadius = 20.dp,
                offsetX = 20.dp,
                offsetY = 20.dp
            )
    ) {
        drawCircle(
            color = surfaceContainer
        )
    }
}

@Composable
fun MidCircleBackgroundGradient(size: Dp, modifier: Modifier = Modifier) {
    val surface = MaterialTheme.colorScheme.surface
    val surfaceContainer = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    Canvas(
        modifier = modifier
            .size(size)
            .circleShadow(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                radius = size,
                blurRadius = 10.dp,
                offsetX = 8.dp,
                offsetY = 15.dp
            )
    ) {
        drawCircle(
            brush = Brush.verticalGradient(
                startY = size.value,
                colors = listOf(
                    surface,
                    surfaceContainer
                )
            )
        )
    }
}

@Composable
fun BottomCircle(size: Dp, modifier: Modifier = Modifier) {
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    //270.dp
    Canvas(
        modifier = modifier
            .size(size)
            .circleShadow(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                radius = size,
                blurRadius = 12.dp,
                offsetX = 2.dp,
                offsetY = 6.dp
            )
            .circleShadow(
                color = MaterialTheme.colorScheme.surfaceBright,
                radius = size,
                blurRadius = 5.dp,
                offsetX = (-2).dp,
                offsetY = (-6).dp
            )
    ) {
        drawCircle(
            brush = Brush.radialGradient(
                center = Offset(x = size.value + 110f, y = size.value + 130f),
                radius = size.value + 150f,
                colors = listOf(
                    surface,
                    surface,
                    surface,
                    surface,
                    surfaceVariant
                )
            )
        )
    }
}

@Composable
fun TimerNumbers(
    timerState: TimerState,
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
                .offset(y = (2f * sizeModifier).dp, x = (1f * sizeModifier).dp),//3
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
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
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = if (isHourVisible) (16f * sizeModifier).sp else (20f * sizeModifier).sp//16//20
        )

        AnimatedVisibility(
            showOvertime,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 23.dp),
                text = "-$overtimeMins:$overtimeSecs.$overtimeMillis",
                fontFamily = Orbitron,
                color = MaterialTheme.colorScheme.primary,
                fontSize = (10f * sizeModifier).sp
            )
        }
    }
}

@Composable
fun ProgressBar(
    sweepAngle: Float,
    diameter: Float,
    sizeModifier: Float
) {
    val primary = MaterialTheme.colorScheme.primary
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    Canvas(
        modifier = Modifier
            .size(diameter.dp)
            .arcShadow(
                color = primary.copy(alpha = 0.5f),
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
                        colors =
                        listOf(
                            primary.copy(alpha = 0.5f),
                            primary,
                            primary,
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
                        colors =
                        listOf(
                            Color.Transparent,
                            Color.Transparent,
                            surfaceBright,
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
                        listOf(
                            Blue,
                            Blue,
                            primary,
                            primary,
                            primary.copy(alpha = 0.5f)
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
    sweepAngle: Float,
    sizeModifier: Float
) {
    var shadowOffsetState by remember {
        mutableFloatStateOf(0f)
    }

    val shadowOffset = animateFloatAsState(
        targetValue = shadowOffsetState,
        animationSpec = tween(easing = LinearEasing), label = "indicatorAnimation"
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

        //Indicator
        Box(
            modifier = modifier
                .rectShadow(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    radius = 0.dp,
                    blurRadius = 2.dp,
                    offsetY = 10.dp,
                    offsetX = 12.dp + shadowOffset.value.dp,
                    spread = 0.dp,
                    modifier = Modifier
                )
                .size(
                    width = (4 * sizeModifier).dp,//4
                    height = (58 * sizeModifier).dp//58
                )
                .background(MaterialTheme.colorScheme.primary)

        )
    }
}

@Composable
fun MidCircle(
    modifier: Modifier = Modifier,
    circleRadius: Float
) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val surface = MaterialTheme.colorScheme.surface
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer

    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    val dividersCount = 100
    val lineLength = circleRadius * 0.08f
    val lineThickness = 2.5f
    val dividersPadding = 4f

    Box(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            circleCenter = Offset(x = width / 2f, y = height / 2f)

            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        surface,
                        surfaceVariant
                    )
                ),
                radius = circleRadius,
                center = circleCenter
            )


            for (i in 0 until dividersCount) {
                val angleInDegrees = i * 360f / dividersCount
                val angleInRad = angleInDegrees * PI / 180f + PI / 2f

                val start = Offset(
                    x = ((circleRadius - dividersPadding) * cos(angleInRad) + circleCenter.x).toFloat(),
                    y = ((circleRadius - dividersPadding) * sin(angleInRad) + circleCenter.y).toFloat()
                )

                val end = Offset(
                    x = ((circleRadius - dividersPadding) * cos(angleInRad) + circleCenter.x).toFloat(),
                    y = ((circleRadius - dividersPadding) * sin(angleInRad) + lineLength + circleCenter.y).toFloat()
                )
                rotate(
                    angleInDegrees + 180,
                    pivot = start
                ) {
                    drawLine(
                        color = surfaceContainer,
                        start = start,
                        end = end,
                        strokeWidth = lineThickness.dp.toPx()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ClockPreview() {
    SoftTImerTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Clock(
                    isDarkTheme = false,
                    timerState = TimerState.Running,
                    duration = Duration.ZERO.plus(5.seconds),
                    hState = 0,
                    minState = 20,
                    sState = 20,
                    overtimeMins = 0,
                    overtimeSecs = 0,
                    overtimeMillis = "0",
                    clockSize = 1f,
                    clockInitialStart = false,
                    progressBarSweepAngle = 0f,
                    showOvertime = false,
                    onClockSizeChange = {},
                    onClockInitialStartChange = {},
                    onProgressBarSweepAngleChange = {},
                    onShowOvertimeChange = {},
                    onClockStartResetAnimationStateChanged = {}
                )

                Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                    BottomCircle(270.dp)
                    MidCircleBackgroundGradient(248.dp)
                    MidCircle(circleRadius = 332.dp.value)
                    FrontCircle(size = 140.dp)
                }
            }
        }
    }
}

