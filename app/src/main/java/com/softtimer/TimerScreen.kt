package com.softtimer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.vectorResource
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
import com.softtimer.util.circleShadow
import com.softtimer.util.offsetFromCenter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimerScreen() {
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
        Clock()

        ActionsSection()
    }
}

@Composable
fun ActionsSection() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        //restart button
        CustomButton(
            size = 32.dp,
            iconSize = 13.dp,
            icon = ImageVector.vectorResource(id = R.drawable.ic_restart),
            contentDescription = "restart"
        )

        //start/stop button
        CustomButton(
            size = 60.dp,
            iconSize = 24.dp,
            icon = ImageVector.vectorResource(id = R.drawable.ic_start),
            contentDescription = "play"
        )

        //theme button
        CustomButton(
            size = 32.dp,
            iconSize = 13.dp,
            icon = ImageVector.vectorResource(id = R.drawable.ic_sun),
            contentDescription = "change theme"
        )
    }
}

@Composable
fun CustomButton(size: Dp, icon: ImageVector, iconSize: Dp, contentDescription: String) {
    Box(
        modifier = Modifier
            .circleShadow(
                color = Shadow,
                radius = size,
                blurRadius = 13.dp,
                offsetX = 6.dp,
                offsetY = 5.dp
            )
            .circleShadow(
                color = Light,
                radius = size,
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
            modifier = Modifier.size(iconSize),
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
    }
}