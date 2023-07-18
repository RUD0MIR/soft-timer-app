package com.softtimer.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.FaintLight
import com.softtimer.ui.theme.FaintLight1
import com.softtimer.ui.theme.FaintShadow
import com.softtimer.ui.theme.FaintShadow1
import com.softtimer.ui.theme.MidAnimationDuration
import com.softtimer.ui.theme.Orbitron
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.getNumbersWithPad
import kotlinx.coroutines.launch

private var visibility by mutableStateOf(1f)
private var isVisible by mutableStateOf(true)

@Composable
fun PickerSection(timerService: TimerService) {
    val timerState = timerService.timerState
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = timerState) {
        when(timerState) {
            TimerState.Started -> {
                //Log.d("TAG", "isVisible: $isVisible")
                animate(
                    initialValue = visibility,
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = MidAnimationDuration,
                        easing = LinearEasing
                    )
                )
                { value, _ ->
                    visibility = value
                    if(visibility < 0.1f) {
                        isVisible = false
                    }
                }
            }
            TimerState.Reset -> {
                isVisible = true
                animate(
                    initialValue = visibility,
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = MidAnimationDuration,
                        easing = LinearEasing
                    )
                )
                { value, _ ->
                    visibility = value
                }
            }
            else -> {}
        }
    }

    if (isVisible) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(155.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                StyledNumberPicker(
                    value = timerService.hState,
                    values = getNumbersWithPad(1..23),
                    name = "h"
                ) { selectedItem, listState->
                    timerService.hState = selectedItem

                }

                StyledNumberPicker(
                    value = timerService.minState,
                    values = getNumbersWithPad(1..59),
                    name = "min"
                ) { selectedItem, listState ->
                    timerService.minState = selectedItem
                    if(timerService.timerState == TimerState.Reset) {
                        scope.launch {
                            listState.scrollToItem(selectedItem)
                        }
                    }
                }

                StyledNumberPicker(
                    value = timerService.sState,
                    values = getNumbersWithPad(1..59),
                    name = "s"
                ) { selectedItem, listState ->
                    timerService.sState = selectedItem
                }
            }
        }
    }
}

@Composable
fun StyledNumberPicker(
    value: Int,
    values: List<String>,
    name: String,
    onValueChanged: (Int, LazyListState) -> Unit
) {
    Box(
        modifier = Modifier.size(width = 60.dp, height = 155.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = name,
            fontFamily = Orbitron,
            fontSize = 16.sp,
            color = Black.copy(visibility),
            fontWeight = FontWeight.Medium
        )

        //NumberPicker
        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 115.dp),
        ) {
            Picker(
                modifier = Modifier
                    .size(width = 50.dp, height = 115.dp)
                    .align(Alignment.Center),
                value = value,
                items = values,
                visibleItemsCount = 3,
                textModifier = Modifier.padding(vertical = 8.dp),
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = Orbitron,
                    color = Black.copy(visibility)
                )
            ) { selectedItem, listState ->
                onValueChanged(selectedItem, listState)
            }


            //shadow effect
            Canvas(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.BottomCenter)
            ) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            FaintShadow.copy(
                                if (visibility - FaintShadow.alpha >= 0)
                                    visibility - FaintShadow.alpha
                                else 0f
                            ),
                            FaintShadow1.copy(
                                if (visibility - FaintShadow1.alpha >= 0)
                                    visibility - FaintShadow1.alpha
                                else 0f
                            ),
                            Color.Transparent
                        )
                    )
                )
            }

            //light effect
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(60.dp)
            ) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            FaintLight1.copy(
                                if (visibility - FaintLight1.alpha >= 0)
                                    visibility - FaintLight1.alpha
                                else 0f
                            ),
                            FaintLight.copy(
                                if (visibility - FaintLight.alpha >= 0)
                                    visibility - FaintLight.alpha
                                else 0f
                            ),
                            Color.Transparent
                        ),
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestPreview() {
    SoftTImerTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFDAD8D8))
        ) {
            //PickerSection()
        }

    }
}