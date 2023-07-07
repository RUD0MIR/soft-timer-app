package com.softtimer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.ui.theme.White1

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
                    surfaceColors
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ClockSection()
    }
}

@Composable
fun ClockSection(modifier: Modifier = Modifier) {
    val dividedCircleColors = listOf(
        Color(0xFFE0DEDE),
        Color(0xFFE0DEDE),
        Color(0xFFCFCFCF)
    )
    val ambientLightColors = listOf(
        Color(0xFFEFEFEF),
        Color(0xFFEFEFEF),
        Color(0xFF878787),
    )

    val shadowColors1 = listOf(
        Color(0xFFABABAC),
        Color.Transparent
    )

    val shadowColors2 = listOf(
        Color(0xFFABABAC),
        Color(0xFFABABAC),
        Color(0xFFABABAC),
        Color(0xFFABABAC),
        Color(0xFFABABAC),
        Color(0xFFABABAC),
        Color(0xFFABABAC),
        Color.Transparent
    )

    val bottomCircleColors = listOf(
        Color(0xFFE2E0E0),
        Color(0xFFE2E0E0),
        Color(0xFFE2E0E0),
        Color(0xFFE2E0E0),
        Color(0xFFE2E0E0),
        Color(0xFFE2E0E0),
        Color(0xFFE2E0E0),
        Color(0xFFE2E0E0),
        Color(0xFFC9C8C8)
    )

    val testColors = listOf(
        Color.Red,
        Color.Red,
        Color.Red,
        Color.Red,
        Color.Red,
        Color.Red,
        Color.Red,
        Color.Red,
        Color.Blue
    )

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
                drawCircle(Brush.radialGradient(
                   // center = Offset(x = 0f, y = 30f),
                    colors = bottomCircleColors
                ))
            }
        )

        //shadow
        Canvas(
            modifier = Modifier.size(180.dp),
            onDraw = {
                val offset = Offset(x = 265f, y = 280f)
                drawCircle(
                    center = offset,
                    brush = Brush.radialGradient(center = offset, colors = shadowColors2)
                )
            }
        )

        //ambient light effect around divided circle
        Canvas(
            modifier = Modifier.size(173.dp),
            onDraw = {
                drawCircle(Brush.verticalGradient(ambientLightColors))
            }
        )

        //divided circle
        Canvas(
            modifier = Modifier.size(170.dp),
            onDraw = {
                drawCircle(Brush.linearGradient(dividedCircleColors))
            }
        )

        //shadow
        Canvas(
            modifier = Modifier.size(110.dp),
            onDraw = {
                val offset = Offset(x = 190f, y = 200f)
                drawCircle(
                    center = offset,
                    brush = Brush.radialGradient(center = offset, colors = shadowColors1)
                )
            }
        )

        //circle with timer
        Canvas(
            modifier = Modifier.size(95.dp),
            onDraw = {
                drawCircle(color = White1)
            }
        )


        Text(text = "15:30", fontSize = 24.sp)
    }


}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    SoftTImerTheme {
        TimerScreen()
    }
}