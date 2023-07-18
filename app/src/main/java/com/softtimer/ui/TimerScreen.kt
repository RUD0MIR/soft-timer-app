package com.softtimer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softtimer.R
import com.softtimer.service.ServiceHelper
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.ButtonGray
import com.softtimer.ui.theme.Light
import com.softtimer.ui.theme.Shadow
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.Constants.ACTION_SERVICE_RESET
import com.softtimer.util.Constants.ACTION_SERVICE_START
import com.softtimer.util.Constants.ACTION_SERVICE_STOP
import com.softtimer.util.circleShadow

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
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(110.dp))

            Clock(timerService = timerService)

            Spacer(modifier = Modifier.height(40.dp))

            PickerSection(timerService = timerService)
        }
        ActionsSection(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp),
            timerService = timerService
        )
    }

}

@Composable
fun ActionsSection(modifier: Modifier = Modifier, timerService: TimerService) {
    val context = LocalContext.current
    val timerState = timerService.timerState
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //restart button
        CustomButton(
            size = 32.dp,
            iconSize = 13.dp,
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
            size = 60.dp,
            iconSize = 24.dp,
            icon = if (timerState == TimerState.Started || timerState == TimerState.Running)
                ImageVector.vectorResource(id = R.drawable.ic_pause)
            else
                ImageVector.vectorResource(id = R.drawable.ic_start),
            contentDescription = if(timerState == TimerState.Running) "pause timer" else "start timer"
        ) {
            ServiceHelper.triggerForegroundService(
                context = context,
                action = if (timerState == TimerState.Running) ACTION_SERVICE_STOP
                else ACTION_SERVICE_START
            )
        }

        //theme button
        CustomButton(
            size = 32.dp,
            iconSize = 13.dp,
            icon = ImageVector.vectorResource(id = R.drawable.ic_sun),
            contentDescription = "change theme"
        ) {
            //TODO change theme
        }
    }
}

@Composable
fun CustomButton(
    size: Dp,
    icon: ImageVector,
    iconSize: Dp,
    contentDescription: String,
    onClick: () -> Unit
) {
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
                onClick()
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
        //TimerScreen()
    }
}