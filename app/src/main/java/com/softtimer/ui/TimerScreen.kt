package com.softtimer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softtimer.R
import com.softtimer.service.ServiceHelper
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.Constants.ACTION_SERVICE_RESET
import com.softtimer.util.Constants.ACTION_SERVICE_START
import com.softtimer.util.Constants.ACTION_SERVICE_STOP

@Composable
fun TimerScreen(
    timerService: TimerService,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        Pair(0.3f, Color(0xFFEAE7E7)),
                        Pair(1f, Color(0xFFC6C6C6))
                    )
                )
            )
            .padding(bottom = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(94.dp))

            Clock(timerService = timerService)

            Spacer(modifier = Modifier.height(16.dp))

            PickerSection(timerService = timerService)

            Spacer(modifier = Modifier.height(154.dp))


        }
        ActionsSection(
            modifier = Modifier.align(Alignment.BottomCenter),
            timerService = timerService
        )
    }
}

@Composable
fun ActionsSection(modifier: Modifier = Modifier, timerService: TimerService) {
    val context = LocalContext.current
    val timerState = timerService.timerState

    Row(
        modifier.fillMaxWidth(0.9f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //restart button
        CustomButton(
            size = 50.dp,
            iconSize = 13.dp,
            iconOffset = Offset(x = -1f, y = -0.3f),
            icon = ImageVector.vectorResource(id = R.drawable.ic_restart),
            contentDescription = "reset timer"
        ) {
            ServiceHelper.triggerForegroundService(
                context = context,
                action = ACTION_SERVICE_RESET
            )
        }

        //start/stop button
        CustomButton(
            size = 90.dp,
            iconSize = 18.dp,
            icon = if (timerState == TimerState.Started || timerState == TimerState.Running)
                ImageVector.vectorResource(id = R.drawable.ic_pause)
            else
                ImageVector.vectorResource(id = R.drawable.ic_start),
            contentDescription = if (timerState == TimerState.Running) "pause timer" else "start timer"
        ) {
            ServiceHelper.triggerForegroundService(
                context = context,
                action = if (timerState == TimerState.Running) ACTION_SERVICE_STOP
                else ACTION_SERVICE_START
            )
        }

        //theme button
        CustomButton(
            size = 50.dp,
            iconSize = 13.dp,
            iconOffset = Offset(x = -0.5f, y = 0f),
            icon = ImageVector.vectorResource(id = R.drawable.ic_sun),
            contentDescription = "change theme"
        ) {
            //TODO change theme
        }
    }
}

@Composable
fun CustomButton(
    size: Dp,//90 to mid
    icon: ImageVector,//18 to mid
    iconOffset: Offset = Offset(0f, 0f),
    iconSize: Dp,
    contentDescription: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(size),
            painter = painterResource(id = R.drawable.button),
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .size(iconSize)
                .offset(x = iconOffset.x.dp, y = iconOffset.y.dp),
            imageVector = icon,
            tint = Black,
            contentDescription = contentDescription,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    SoftTImerTheme {
        ActionsSection(timerService = TimerService())
    }
}