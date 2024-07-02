package com.softtimer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "Clock1"

@Composable
fun Clock(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    duration: Duration,
    hState: Int,
    minState: Int,
    sState: Int,
    overtimeMins: Int,
    overtimeSecs: Int,
    overtimeMillis: String,
    clockSizeModifier: Float,
    clockInitialStart: Boolean,
    progressBarSweepAngle: Float,
    showOvertime: Boolean,
    onClockSizeModifierChange: (Float) -> Unit,
    onClockInitialStartChange: (Boolean) -> Unit,
    onProgressBarSweepAngleChange: (Float) -> Unit,
    onShowOvertimeChange: (Boolean) -> Unit,
    onClockStartResetAnimationStateChanged: (Boolean) -> Unit,
) {
    val clockSizeModifierValue by animateFloatAsState(
        targetValue = clockSizeModifier,
        animationSpec = tween(
            durationMillis = MID_ANIMATION_DURATION,
            easing = LinearEasing
        ), label = "clockSizeAnimation"
    )

    val clockSize by remember {
        derivedStateOf {
            220.dp * clockSizeModifierValue
        }
    }


    LaunchedEffect(key1 = timerState) {
        when (timerState) {
            TimerState.Idle -> {
                onClockInitialStartChange(true)
                onClockSizeModifierChange(CLOCK_MIN_SIZE)
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
                    onClockSizeModifierChange(CLOCK_MAX_SIZE)
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
                    targetValue = -360f,
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
            .size(clockSize),
        contentAlignment = Alignment.Center
    ) {
        BottomCircle(clockSize)

        ProgressBar(
            sweepAngle = progressBarSweepAngle,
            size = clockSize * 0.99f,
            sizeModifier = clockSizeModifierValue
        )

        MidCircleBackgroundGradient(clockSize * 0.84f )
        MidCircle(size = clockSize * 0.4f)

        Indicator(
            modifier = Modifier.offset(y = -clockSize * 0.25f),
            size = clockSize * 0.5f,
            sweepAngle = progressBarSweepAngle,
        )

        FrontCircle(size = clockSize * 0.47f)

        TimerNumbers(
            timerState = timerState,
            hours = hState.absPad(),
            minutes = minState.absPad(),
            seconds = sState.absPad(),
            showOvertime = showOvertime,
            overtimeMins = overtimeMins.absPad(),
            overtimeSecs = overtimeSecs.absPad(),
            overtimeMillis = overtimeMillis,
            sizeModifier = clockSizeModifierValue
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
    size: Dp,
    sizeModifier: Float
) {
    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    Canvas(
        modifier = Modifier
            .size(size)
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
                //progress bar
                drawArc(
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    brush = Brush.sweepGradient(
                        colors =
                        listOf(
                            surface.copy(alpha = 0.5f),
                            surface.copy(alpha = 0.3f),
                            primary.copy(alpha = 0.9f),
                            primary.copy(alpha = 0.9f),
                            primary.copy(alpha = 0.9f),
                        ),
                        center = center
                    )
                )

                //flash effect
                drawArc(
                    startAngle = 0f,
                    sweepAngle = if (sweepAngle <= 30f) sweepAngle else 60f,
                    useCenter = true,
                    brush = Brush.sweepGradient(
                        colors =
                        listOf(
                            Color.Transparent,
                            surfaceBright.copy(alpha = 0.5f),
                            surfaceBright,
                            surfaceBright.copy(alpha = 0.5f),
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
                    useCenter = false,
                    brush = Brush.horizontalGradient(
                        startX = size.toPx() - 600,
                        colors = listOf(
                            primary,
                            primary.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Transparent,
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
    size: Dp
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
            .size(width = size, height = size),//100.dp
        contentAlignment = Alignment.Center
    ) {
        //Indicator shadow
        Box(
            modifier = modifier
                .size(
                    width = (2).dp,
                    height = (size)
                )
                .offset(x = (-3).dp, y = (-3).dp)
                .rectShadow(
                    color = MaterialTheme.colorScheme.onSurface,
                    blurRadius = 3.dp,
                )
        )

        //Indicator
        Box(
            modifier = modifier
                .size(
                    width = (3).dp,//4
                    height = (size)//58
                )
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun MidCircle(
    modifier: Modifier = Modifier,
    size: Dp
) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val surface = MaterialTheme.colorScheme.surface
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer

    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    val dividersCount = 100

    val lineThickness = size * 0.015f
    val dividersPadding = 4f

    Canvas(
        modifier = modifier
            .size(size)
    ) {
        val lineLength = size.toPx() * 0.08f
        val width = this.size.width
        val height = this.size.height

        circleCenter = Offset(x = width / 2f, y = height / 2f)

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    surface,
                    surfaceVariant
                )
            ),
            radius = size.toPx(),
            center = circleCenter
        )


        for (i in 0 until dividersCount) {
            val angleInDegrees = i * 360f / dividersCount
            val angleInRad = angleInDegrees * PI / 180f + PI / 2f

            val start = Offset(
                x = ((size.toPx() - dividersPadding) * cos(angleInRad) + circleCenter.x).toFloat(),
                y = ((size.toPx() - dividersPadding) * sin(angleInRad) + circleCenter.y).toFloat()
            )

            val end = Offset(
                x = ((size.toPx() - dividersPadding) * cos(angleInRad) + circleCenter.x).toFloat(),
                y = ((size.toPx() - dividersPadding) * sin(angleInRad) + lineLength + circleCenter.y).toFloat()
            )
            rotate(
                angleInDegrees + 180,
                pivot = start
            ) {
                drawLine(
                    color = surfaceContainer,
                    start = start,
                    end = end,
                    strokeWidth = lineThickness.toPx()
                )
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
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Clock(
                    timerState = TimerState.Running,
                    duration = Duration.ZERO.plus(5.seconds),
                    hState = 0,
                    minState = 20,
                    sState = 20,
                    overtimeMins = 0,
                    overtimeSecs = 0,
                    overtimeMillis = "0",
                    clockSizeModifier = 1f,
                    clockInitialStart = false,
                    progressBarSweepAngle = 280f,
                    showOvertime = false,
                    onClockSizeModifierChange = {},
                    onClockInitialStartChange = {},
                    onProgressBarSweepAngleChange = {},
                    onShowOvertimeChange = {},
                    onClockStartResetAnimationStateChanged = {}
                )
            }
        }
    }
}

