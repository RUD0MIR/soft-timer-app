package com.softtimer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.ButtonGray
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
fun TimerScreen() {

    val surfaceColors = listOf(
        Color(0xFFEAE7E7),
        Color(0xFFEAE7E7),
        Color(0xFFE6E3E3),
        Color(0xFFDAD9D9),
        Color(0xFFC6C6C6)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        Pair(0.3f, Color(0xFFEAE7E7)),
                        Pair(1f, Color(0xFFC6C6C6))
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ClockSection()

        ActionsSection()
    }
}

@Composable
fun ClockSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        contentAlignment = Alignment.Center
    ) {
        //bottom circle
        Canvas(
            modifier = Modifier.size(size = 210.dp),
            onDraw = {
                drawCircle(
                    Brush.radialGradient(
                        center = Offset.offsetFromCenter(x = 50f, y = 60f, center = center),
                        colorStops = arrayOf(
                            Pair(0.8f, Color(0xFFE2E0E0)),
                            Pair(0.9f, Color(0xFFD9D7D7)),
                            Pair(1f, Color(0xFFC9C8C8))
                        ),
                        radius = 350f
                    )
                )
            }
        )

        //shadow
        Canvas(
            modifier = Modifier.size(180.dp),
            onDraw = {
                val offset = Offset(x = 265f, y = 280f)
                drawCircle(
                    center = offset,
                    brush = Brush.radialGradient(
                        center = offset,
                        colorStops = arrayOf(
                            Pair(0.8f, Color(0xFFABABAC)),
                            Pair(1f, Color.Transparent)
                        ),
                    )
                )
            }
        )

        //ambient light effect around divided circle
        Canvas(
            modifier = Modifier.size(173.dp),
            onDraw = {
                drawCircle(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            Pair(0.5f, Light),
                            Pair(1f, Color(0xFF878787))
                        )
                    )
                )
            }
        )

        //divided circle
        Canvas(
            modifier = Modifier.size(170.dp),
            onDraw = {
                drawCircle(
                    Brush.linearGradient(
                        colorStops = arrayOf(
                            Pair(0.4f, Color(0xFFE0DEDE)),
                            Pair(1f, Color(0xFFCFCFCF))
                        )
                    )
                )
            }
        )

        TimerDividers(circleRadius = 230f)

        //shadow
        Canvas(
            modifier = Modifier.size(110.dp),
            onDraw = {
                val offset = Offset(x = 190f, y = 200f)
                drawCircle(
                    center = offset,
                    brush = Brush.radialGradient(
                        center = offset,
                        colorStops = arrayOf(
                            Pair(0.3f, Color(0xFFABABAC)),
                            Pair(1f, Color.Transparent)
                        )
                    )
                )
            }
        )

        //indicator
        Canvas(
            modifier = Modifier
                .shadow(elevation = 5.dp)
                .size(width = 4.dp, height = 58.dp),
            onDraw = {
                //shadow
//                drawRect(
//                    brush = Brush.horizontalGradient(
//                        colorStops = arrayOf(
//                            Pair(0.9f, Color(0xFFA09E9E)),
//                            Pair(1f, Color.Green),
//                        ),
//                        startX = 1f,
//                        endX = 1f
//                    ),
//                    size = Size(width = 5.dp.toPx(), height = 58.dp.toPx()),
//                    topLeft = Offset.offsetFromCenter(x = 1f, y = -280f, center = center)
//                )

                //indicator
                drawRect(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            Pair(1f, Color(0xFF305FD6)),
                            Pair(0.3f, Color(0xFF6E8EDF))
                        )
                    ),
                    size = Size(width = 4.dp.toPx(), height = 58.dp.toPx()),
                    topLeft = Offset.offsetFromCenter(x = -4f, y = -289f, center = center)
                )
            }
        )

        Indicator(modifier = Modifier.offset(y = (-76).dp))

        //circle with timer
        Canvas(
            modifier = Modifier.size(95.dp),
            onDraw = {
                drawCircle(color = White1)
            }
        )

        TimerNumbers()
    }
}

@Composable
fun TimerNumbers() {
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
        circleCenter = Offset(x = size.width/2f, y = size.height/2f)
        val lineLength = 16f
        val dividersCount = 80

        for(i in 0 until dividersCount){
            val angleInDegrees = i*360f/dividersCount
            val angleInRad = angleInDegrees * PI / 180f + PI /2f
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
                angleInDegrees+180,
                pivot = start
            ){
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
        Box(modifier = modifier
            .shadow(
                modifier = Modifier
                    .rotate(4f),
                blurRadius = 4.dp,
                offsetX = 10.dp,
                offsetY = 6.dp,
                color = Color(0xFFA1A0A0)
            )
            .size(width = 4.dp, height = 58.dp)
        )


        Box(modifier = modifier
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

@Composable
fun ActionsSection() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        //restart button
        CustomButton(size = 32.dp, icon = Icons.Default.Refresh, "restart")

        //start/stop button
        CustomButton(size = 60.dp, icon = Icons.Default.PlayArrow, "play")

        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Composable
fun CustomButton(size: Dp, icon: ImageVector, contentDescription: String) {
    Box(modifier = Modifier
        .shadow(
            shape = CircleShape,
            color = Shadow,
            borderRadius = size,
            blurRadius = 13.dp,
            offsetX = 6.dp,
            offsetY = 5.dp
        )
        .shadow(
            shape = CircleShape,
            color = Light,
            borderRadius = size,
            blurRadius = 10.dp,
            offsetX = (-6).dp,
            offsetY = (-5).dp
        )
        .size(size)
        .clip(CircleShape)
        .background(ButtonGray)
        .clickable {

        },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(size / 2),
            tint = Black,
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    SoftTImerTheme {
        TimerScreen()
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            ActionsSection()
//        }
    }
}