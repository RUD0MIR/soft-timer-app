package com.softtimer.ui

import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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

private var pickerVisibility by mutableStateOf(1f)
private var isVisible by mutableStateOf(true)


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PickerSection(timerService: TimerService) {
    val timerState = timerService.timerState
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = timerState) {
        when (timerState) {
            TimerState.Started -> {
                animate(
                    initialValue = pickerVisibility,
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = MidAnimationDuration,
                        easing = LinearEasing
                    )
                )
                { value, _ ->
                    pickerVisibility = value
                    if (pickerVisibility < 0.1f) {
                        isVisible = false
                    }
                }
            }

            TimerState.Reset -> {
                isVisible = true
                animate(
                    initialValue = pickerVisibility,
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = MidAnimationDuration,
                        easing = LinearEasing
                    )
                )
                { value, _ ->
                    pickerVisibility = value
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
                ) { selectedItem ->
                    timerService.hState = selectedItem
                }

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 34.dp),
                    text = ":",
                    fontFamily = Orbitron,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                StyledNumberPicker(
                    value = timerService.minState,
                    values = getNumbersWithPad(1..59),
                    name = "min"
                ) { selectedItem ->
                    timerService.minState = selectedItem
                }

                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 34.dp),
                    text = ":",
                    fontFamily = Orbitron,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                StyledNumberPicker(
                    value = timerService.sState,
                    values = getNumbersWithPad(1..59),
                    name = "s"
                ) { selectedItem ->
                    timerService.sState = selectedItem
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun StyledNumberPicker(
    value: Int,
    values: List<String>,
    name: String,
    onValueChanged: (Int) -> Unit
) {
    Box(
        modifier = Modifier.size(width = 50.dp, height = 140.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = name,
            fontFamily = Orbitron,
            fontSize = 16.sp,
            color = Black.copy(pickerVisibility),
            fontWeight = FontWeight.SemiBold
        )

        //NumberPicker
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.80f),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    NumberPicker(context).apply {
                        minValue = 0
                        maxValue = values.size - 1
                        displayedValues = values.toTypedArray()
                        textColor = Black.copy(pickerVisibility).hashCode()
                        textSize = 60f
                        setOnValueChangedListener { numberPicker, i, i2 ->
                            onValueChanged(numberPicker.value)
                        }
                    }
                },
                update = { picker ->
                    picker.apply {
                        textColor = Black.copy(pickerVisibility).hashCode()
                    }

                }
            )

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
                                if (pickerVisibility - FaintShadow.alpha >= 0)
                                    pickerVisibility - FaintShadow.alpha
                                else 0f
                            ),
                            FaintShadow1.copy(
                                if (pickerVisibility - FaintShadow1.alpha >= 0)
                                    pickerVisibility - FaintShadow1.alpha
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
                                if (pickerVisibility - FaintLight1.alpha >= 0)
                                    pickerVisibility - FaintLight1.alpha
                                else 0f
                            ),
                            FaintLight.copy(
                                if (pickerVisibility - FaintLight.alpha >= 0)
                                    pickerVisibility - FaintLight.alpha
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
        var value by remember { mutableStateOf("00") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFDAD8D8)),
            contentAlignment = Alignment.Center
        ) {

        }
    }
}