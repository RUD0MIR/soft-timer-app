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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.softtimer.ExpiredActivity
import com.softtimer.service.ServiceHelper
import com.softtimer.service.TimerService
import com.softtimer.ui.Clock
import com.softtimer.ui.RestartButton
import com.softtimer.util.Constants
import com.softtimer.util.Constants.CLOCK_MAX_SIZE
import kotlin.time.Duration

@Composable
fun ExpiredScreen(
    activity: ExpiredActivity,
    timerService: TimerService
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        Pair(0.3f, Color(0xFFEAE7E7)),
                        Pair(1f, Color(0xFFC6C6C6))
                    )
                ),
            )
            .padding(bottom = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (clock) = createRefs()
            val topGuideLine = createGuidelineFromTop(fraction = 0.16f)

            Clock(
                modifier = Modifier.constrainAs(clock) {
                    top.linkTo(topGuideLine)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                timerService = timerService,
                clockSize = CLOCK_MAX_SIZE,
                onClockSizeChanged = {},
                onClockAnimationStateChanged = {},
            )
        }

        RestartButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            size = 90.dp
        ) {
            ServiceHelper.triggerForegroundService(
                context = context,
                action = Constants.ACTION_SERVICE_RESET
            )

            activity.finish()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpiredScreenPreview() {
    SoftTImerTheme {
    }
}
