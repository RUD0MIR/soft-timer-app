package com.softtimer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Clock(modifier: Modifier = Modifier) {
    var sweepAngle by remember {
        mutableStateOf(360f)
    }
    var shadowPosition by remember {
        mutableStateOf(0f)
    }

    var minValue by remember {
        mutableStateOf(17)
    }
    var secValue by remember {
        mutableStateOf(30)
    }
    var timeValue by remember {
        mutableStateOf("${minValue}:${secValue}")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentAlignment = Alignment.Center
    ) {
        BottomCircle()

        ProgressBar(sweepAngle = sweepAngle, diameter = 210.dp)

        MidCircle()

        TimerDividers(diameter = 230f)

        Indicator(modifier = Modifier.offset(y = (-76).dp), sweepAngle = sweepAngle) {}

        TimerNumbers(timeValue)
    }
}

@Composable
fun BottomCircle() {
    Canvas(
        modifier = Modifier
            .circleShadow(
                color = Shadow,
                radius = 216.dp,
                blurRadius = 10.dp,
                offsetX = 5.dp,
                offsetY = 5.dp
            )
            .circleShadow(
                color = Light,
                radius = 210.dp,
                blurRadius = 10.dp,
                offsetY = (-5).dp
            )
            .size(210.dp)
            .clip(CircleShape),
        onDraw = {
            drawCircle(
                Brush.radialGradient(
                    center = Offset.offsetFromCenter(x = 20f, y = 70f, center = center),
                    colorStops = arrayOf(
                        Pair(0.8f, Color(0xFFE2E0E0)),
                        Pair(1f, Color(0xFFC9C8C8))
                    ),
                    radius = 370f
                )
            )
        }
    )
}

@Composable
fun MidCircle() {
    Canvas(
        modifier = Modifier
            .size(170.dp)
            .circleShadow(
                color = DarkShadow,
                radius = 178.dp,
                blurRadius = 7.dp,
                offsetX = 6.dp,
                offsetY = 10.dp
            ),
        onDraw = {
            //ambient light & shadow
            drawCircle(
                radius = 173.dp.toPx() / 2,
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        Pair(0.2f, Light),
                        Pair(1f, Color(0xFF878787))
                    )
                )
            )

            //circle itself
            drawCircle(
                radius = 170.dp.toPx() / 2,
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
fun TimerNumbers(timeValue: String) {
    Canvas(
        modifier = Modifier
            .size(95.dp)
            .circleShadow(
                color = Light,
                radius = 95.dp,
                blurRadius = 15.dp,
                offsetX = (-4).dp,
                offsetY = (-2).dp
            )
            .circleShadow(
                color = Shadow,
                radius = 95.dp,
                blurRadius = 15.dp,
                offsetX = 12.dp,
                offsetY = 16.dp
            ),
        onDraw = {
            drawCircle(color = White1)
        }
    )
    Text(
        modifier = Modifier
            .offset(y = 3.dp),
        text = timeValue,
        fontFamily = Orbitron,
        color = FaintShadow,
        fontSize = 20.sp
    )

    Text(
        text = timeValue,
        fontFamily = Orbitron,
        color = Black,
        fontSize = 20.sp
    )
}

@Composable
fun TimerDividers(diameter: Float) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        circleCenter = Offset(x = size.width / 2f, y = size.height / 2f)
        val lineLength = 15f
        val dividersCount = 96

        for (i in 0 until dividersCount) {
            val angleInDegrees = i * 360f / dividersCount
            val angleInRad = angleInDegrees * PI / 180f + PI / 2f
            val lineThickness = 0.8f

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
fun ProgressBar(sweepAngle: Float, diameter: Dp) {
    Canvas(
        modifier = Modifier
            .size(diameter)
            .arcShadow(
                color = BlueLight,
                startAngle = 0f,
                useCenter = true,
                spread = 2.dp,
                sweepAngle = sweepAngle,
                blurRadius = 4.dp,
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
                    size = Size(width = diameter.toPx(), height = diameter.toPx()),
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
                    sweepAngle = 30f,
                    size = Size(width = diameter.toPx(), height = diameter.toPx()),
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
                    style = Stroke(width = 2.dp.toPx()),
                    size = Size(width = diameter.toPx(), height = diameter.toPx()),
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
fun Indicator(modifier: Modifier = Modifier, sweepAngle: Float, onAngleChanged: () -> Unit) {
    Box(
        modifier = Modifier
            .rotate(sweepAngle)
            .size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .rectShadow(
                    modifier = Modifier
                        .rotate(2f),
                    blurRadius = 2.dp,
                    spread = 2.dp,
                    offsetX = 10.dp,
                    offsetY = 7.dp,
                    color = DarkShadow
                )
                .size(width = 1.dp, height = 58.dp)
        )

        Box(
            modifier = modifier
                .background(Blue)
                .size(width = 4.dp, height = 58.dp)

        )
    }
    onAngleChanged()
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
            Clock()
        }

    }
}
