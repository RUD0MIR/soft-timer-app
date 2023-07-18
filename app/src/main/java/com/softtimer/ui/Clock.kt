package com.softtimer.ui

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.Blue
import com.softtimer.ui.theme.BlueFlash
import com.softtimer.ui.theme.BlueLight
import com.softtimer.ui.theme.DarkShadow
import com.softtimer.ui.theme.FaintShadow
import com.softtimer.ui.theme.Light
import com.softtimer.ui.theme.Orbitron
import com.softtimer.ui.theme.Shadow
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.ui.theme.White1
import com.softtimer.ui.theme.LightBlue
import com.softtimer.ui.theme.MidBlue
import com.softtimer.util.arcShadow
import com.softtimer.util.circleShadow
import com.softtimer.util.offsetFromCenter
import com.softtimer.util.rectShadow
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private var sizeModifier by mutableStateOf(1f)
private var progressBarSweepAngle by mutableStateOf(0f)

@Composable
fun Clock(
    modifier: Modifier = Modifier,
    timerService: TimerService,
) {
    val progressBarDelay = 2000L
    val animationsDuration = progressBarDelay.toInt() - 150
    val durationInMillis = timerService.duration.inWholeMilliseconds.toInt()

    var shadowPosition by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(timerService.timerState) {
        //val remainTimeMillis = viewModel.remainTime * 1000
        when(timerService.timerState) {
            TimerState.Started -> {
                awaitAll(
                    async {
                        //progress bar starting animation
                        animate(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = tween(
                                durationMillis = animationsDuration,
                                easing = LinearEasing
                            )
                        )
                        { value, _ ->
                            progressBarSweepAngle = value
                        }
                    },
                    //clock zoom animation
                    async {
                        animate(
                            initialValue = sizeModifier,
                            targetValue = 1.4f,
                            animationSpec = tween(
                                durationMillis = animationsDuration,
                                easing = LinearEasing
                            )
                        )
                        { value, _ ->
                            sizeModifier = value
                        }
                    },
                )
            }
            TimerState.Running -> {
                //progress bar running animation
                animate(
                    initialValue = progressBarSweepAngle,
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = durationInMillis, easing = LinearEasing)
                )
                { value, _ ->
                    progressBarSweepAngle = value
                }
            }
            TimerState.Reset -> {
//progress bar reset animation
                awaitAll(
                    async {
                        animate(
                            initialValue = progressBarSweepAngle,
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = animationsDuration,
                                easing = LinearEasing
                            )
                        )
                        { value, _ ->
                            progressBarSweepAngle = value
                        }
                    },
                    async {
                        animate(
                            initialValue = sizeModifier,
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = animationsDuration,
                                easing = LinearEasing
                            )
                        )
                        { value, _ ->
                            sizeModifier = value
                        }
                    }
                )
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height((250f * sizeModifier).dp),
        contentAlignment = Alignment.Center
    ) {
        BottomCircle(diameter = 210f * sizeModifier)//210

        ProgressBar(
            sweepAngle = progressBarSweepAngle,
            diameter = 210f * sizeModifier//210
        )

        MidCircle(diameter = 170f * sizeModifier)//170

        TimerDividers(diameter = 230f * sizeModifier)//230

        Indicator(
            modifier = Modifier.offset(y = (-76f * sizeModifier).dp),
            size = 100 * sizeModifier,//100
            sweepAngle = progressBarSweepAngle
        )

        TimerNumbers(
            timerService = timerService,
            size = 95 * sizeModifier//95
        )
    }
}


@Composable
fun BottomCircle(diameter: Float) {
    Canvas(
        modifier = Modifier
            .circleShadow(
                color = Shadow,
                radius = (216f * sizeModifier).dp,//216
                blurRadius = (10f * sizeModifier).dp,//10
                offsetX = (5f * sizeModifier).dp,//5
                offsetY = (5f * sizeModifier).dp,//5
            )
            .circleShadow(
                color = Light,
                radius = diameter.dp,//210
                blurRadius = (10f * sizeModifier).dp,//10
                offsetY = -(5f * sizeModifier).dp,//-5
            )
            .size(diameter.dp)
            .clip(CircleShape),
        onDraw = {
            drawCircle(
                Brush.radialGradient(
                    center = Offset
                        .offsetFromCenter(
                            x = 20f * sizeModifier,//20
                            y = 70f * sizeModifier,//70
                            center = center
                        ),
                    colorStops = arrayOf(
                        Pair(0.8f, Color(0xFFE2E0E0)),
                        Pair(1f, Color(0xFFC9C8C8))
                    ),
                    radius = 370f * sizeModifier//370
                )
            )
        }
    )
}

@Composable
fun MidCircle(diameter: Float) {
    Canvas(
        modifier = Modifier
            .size(diameter.dp)//170
            .circleShadow(
                color = DarkShadow,
                radius = (178f * sizeModifier).dp,//178
                blurRadius = (7f * sizeModifier).dp,//7
                offsetX = (6f * sizeModifier).dp,//6
                offsetY = (10f * sizeModifier).dp//10
            ),
        onDraw = {
            //ambient light & shadow
            drawCircle(
                radius = (173f * sizeModifier).dp.toPx() / 2,
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        Pair(0.2f, Light),
                        Pair(1f, Color(0xFF878787))
                    )
                )
            )

            //circle itself
            drawCircle(
                radius = diameter.dp.toPx() / 2,
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        Pair(0.4f, Color(0xFFE0DEDE)),
                        Pair(1f, Color(0xFFCFCFCF))
                    )
                )
            )
        }
    )
}

@Composable
fun TimerNumbers(size: Float, timerService: TimerService) {
    val isHourVisible = timerService.hState != 0
    val hours = timerService.getH()
    val minutes = timerService.getMin()
    val seconds = timerService.getS()

    Canvas(
        modifier = Modifier
            .size(size.dp)//95
            .circleShadow(
                color = Light,
                radius = size.dp,
                blurRadius = (15f * sizeModifier).dp,//15
                offsetX = -(4f * sizeModifier).dp,//-4
                offsetY = -(2f * sizeModifier).dp//-2
            )
            .circleShadow(
                color = Shadow,
                radius = (95f * sizeModifier).dp,//95
                blurRadius = (15f * sizeModifier).dp,//15
                offsetX = (12f * sizeModifier).dp,//12
                offsetY = (16f * sizeModifier).dp//16
            ),
        onDraw = {
            drawCircle(color = White1)
        }
    )

    Text(
        modifier = Modifier
            .offset(y = (3f * sizeModifier).dp),//3
        text = buildString {
            if (isHourVisible) append("${hours}:")
            append(minutes)
            append(":")
            append(seconds)
        },
        fontFamily = Orbitron,
        color = FaintShadow,
        fontSize = if (isHourVisible) (16f * sizeModifier).sp else (20f * sizeModifier).sp//16//20
    )

    Text(
        text = buildString {
            if (isHourVisible) append("${hours}:")
            append(minutes)
            append(":")
            append(seconds)
        },
        fontFamily = Orbitron,
        color = Black,
        fontSize = if (isHourVisible) (16f * sizeModifier).sp else (20f * sizeModifier).sp//16//20
    )
}

@Composable
fun TimerDividers(diameter: Float) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        circleCenter = Offset(x = size.width / 2f, y = size.height / 2f)
        val lineLength = 15f * sizeModifier
        val dividersCount = 96

        for (i in 0 until dividersCount) {
            val angleInDegrees = i * 360f / dividersCount
            val angleInRad = angleInDegrees * PI / 180f + PI / 2f
            val lineThickness = 0.8f * sizeModifier

            val start = Offset(
                x = (diameter * cos(angleInRad) + circleCenter.x).toFloat(),
                y = (diameter * sin(angleInRad) + circleCenter.y).toFloat()
            )

            val end = Offset(
                x = (diameter * cos(angleInRad) + circleCenter.x).toFloat(),
                y = (diameter * sin(angleInRad) + lineLength + circleCenter.y).toFloat()
            )
            rotate(
                angleInDegrees + 180,
                pivot = start
            ) {
                drawLine(
                    color = Color(0xFFB8B8B8),
                    start = start,
                    end = end,
                    strokeWidth = lineThickness.dp.toPx()
                )
            }
        }
    }
}

@Composable
fun ProgressBar(sweepAngle: Float, diameter: Float) {
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
fun Indicator(modifier: Modifier = Modifier, sweepAngle: Float, size: Float) {
    Box(
        modifier = Modifier
            .rotate(sweepAngle)
            .size(size.dp),//100.dp
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .rectShadow(
                    modifier = Modifier
                        .rotate(2f),
                    blurRadius = (2 * sizeModifier).dp,//2
                    spread = (2 * sizeModifier).dp,//2
                    offsetX = (10 * sizeModifier).dp,//10
                    offsetY = (7 * sizeModifier).dp,//7
                    color = DarkShadow
                )
                .size(
                    width = (1f * sizeModifier).dp,
                    height = (58f * sizeModifier).dp
                )//width = 1, height = 58.dp
        )

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

@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    SoftTImerTheme {
        Box(
            Modifier
                .size(500.dp)
                .background(Color(0xFFD8D6D6)),
            contentAlignment = Alignment.Center
        ) {

        }
    }
}
