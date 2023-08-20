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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.softtimer.ExpiredActivity
import com.softtimer.TimerViewModel
import com.softtimer.service.TimerService
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
                viewModel = viewModel,
                timerState = timerService.timerState,
                sizeModifier = CLOCK_MAX_SIZE,
                timerNumbers = {
                    TimerNumbers(
                        timerState = timerService.timerState,
                        hours = timerService.hState,
                        minutes = timerService.minState,
                        seconds = timerService.sState,
                        sizeModifier = CLOCK_MAX_SIZE
                    )
                },
                onTimerStateChanged = {}
            )
        }

        RestartButton(size = 90.dp) {
            activity.finish()
        }
    }
}
