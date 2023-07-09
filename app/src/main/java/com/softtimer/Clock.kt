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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.Light
import com.softtimer.ui.theme.Orbitron
import com.softtimer.ui.theme.Shadow
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.ui.theme.White1
import com.softtimer.util.offsetFromCenter
import com.softtimer.util.shadow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Clock(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentAlignment = Alignment.Center
    ) {
        BottomCircle()

        MidCircle()

        TimerDividers(circleRadius = 230f)

        Indicator(modifier = Modifier.offset(y = (-76).dp))

        TimerNumbers()
    }
}

@Composable
fun BottomCircle() {
    Canvas(
        modifier = Modifier
            .shadow(
                shape = CircleShape,
                color = Shadow,
                radius = 217.dp,
                blurRadius = 13.dp
            )
            .shadow(
                shape = CircleShape,
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
    //divided circle
    Canvas(
        modifier = Modifier
            .size(170.dp)
            .shadow(
                shape = CircleShape,
                color = Shadow,
                radius = 170.dp,
                blurRadius = 10.dp,
                offsetX = 10.dp,
                offsetY = 15.dp
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
fun TimerNumbers() {
    Canvas(
        modifier = Modifier
            .size(95.dp)
            .shadow(
                shape = CircleShape,
                color = Light,
                radius = 95.dp,
                blurRadius = 15.dp,
                offsetX = (-4).dp,
                offsetY = (-2).dp
            )
            .shadow(
                shape = CircleShape,
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
        text = "15:30",
        fontFamily = Orbitron,
        color = Black,
        fontSize = 20.sp
    )
}

@Composable
fun TimerDividers(circleRadius: Float) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        circleCenter = Offset(x = size.width / 2f, y = size.height / 2f)
        val lineLength = 16f
        val dividersCount = 80

        for (i in 0 until dividersCount) {
            val angleInDegrees = i * 360f / dividersCount
            val angleInRad = angleInDegrees * PI / 180f + PI / 2f
            val lineThickness = 1.3f

            val start = Offset(
                x = (circleRadius * cos(angleInRad) + circleCenter.x).toFloat(),
                y = (circleRadius * sin(angleInRad) + circleCenter.y).toFloat()
            )

            val end = Offset(
                x = (circleRadius * cos(angleInRad) + circleCenter.x).toFloat(),
                y = (circleRadius * sin(angleInRad) + lineLength + circleCenter.y).toFloat()
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
fun Indicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(
                modifier = Modifier
                    .rotate(3f),
                blurRadius = 4.dp,
                spread = 5.dp,
                offsetX = 10.dp,
                offsetY = 10.dp,
                color = Color(0xFFA1A0A0)
            )
            .size(width = 4.dp, height = 58.dp)
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF305FD6),
                        Color(0xFF6E8EDF)
                    ),
                    startY = 90f
                )
            )
            .size(width = 4.dp, height = 58.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    SoftTImerTheme {
        Box(Modifier.size(500.dp), contentAlignment = Alignment.Center) {
            Clock()
        }

    }
}
