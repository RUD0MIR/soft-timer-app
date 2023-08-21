package com.softtimer.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.softtimer.ExpiredActivity
import com.softtimer.TimerViewModel
import com.softtimer.service.TimerService
import com.softtimer.timerService
import com.softtimer.ui.ActionsSection
import com.softtimer.ui.Clock
import com.softtimer.ui.RestartButton
import com.softtimer.ui.TimerNumbers
import com.softtimer.util.Constants.CLOCK_MAX_SIZE

@Composable
fun ExpiredScreen(
    timerService: TimerService,
    viewModel: TimerViewModel,
    activity: ExpiredActivity
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
//                brush = Brush.linearGradient(
//                    colorStops = arrayOf(
//                        Pair(0.3f, Color(0xFFEAE7E7)),
//                        Pair(1f, Color(0xFFC6C6C6))
//                    )
//                )
                color = Color.Red
            )
            .padding(bottom = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Clock(
                clockSize = CLOCK_MAX_SIZE,
                onClockSizeChanged = {},
                onClockAnimationStateChanged = {},
                onClockInitialStart = {}
            )
        }

        RestartButton(size = 90.dp) {
            activity.finish()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    SoftTImerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.Red
                )
                .padding(bottom = 64.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Clock(
                    clockSize = CLOCK_MAX_SIZE,
                    onClockSizeChanged = {},
                    onClockAnimationStateChanged = {},
                    onClockInitialStart = {}
                )
            }

            RestartButton(size = 90.dp) {
            }
        }
    }
}