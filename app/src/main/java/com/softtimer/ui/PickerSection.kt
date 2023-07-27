package com.softtimer.ui

import android.widget.NumberPicker
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.softtimer.R
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.FaintLight
import com.softtimer.ui.theme.FaintLight1
import com.softtimer.ui.theme.FaintShadow
import com.softtimer.ui.theme.MID_ANIMATION_DURATION
import com.softtimer.ui.theme.Orbitron
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.getNumbersWithPad

private var pickerVisibility by mutableStateOf(1f)
private var isVisible by mutableStateOf(true)

@Composable
fun PickerSection(modifier: Modifier = Modifier,timerService: TimerService) {
    val timerState = timerService.timerState
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = timerState) {
        when (timerState) {
            TimerState.Started -> {
                animate(
                    initialValue = pickerVisibility,
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = MID_ANIMATION_DURATION,
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
                        durationMillis = MID_ANIMATION_DURATION,
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
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
                .height(139.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                StyledNumberPicker(
                    modifier = Modifier.offset(x = 14.dp),
                    value = timerService.hState,
                    values = getNumbersWithPad(1..23),
                    showDivider = true,
                    name = "h"
                ) { selectedItem ->
                    timerService.hState = selectedItem
                }

                StyledNumberPicker(
                    value = timerService.minState,
                    values = getNumbersWithPad(1..59),
                    name = "min",
                    showDivider = true,
                ) { selectedItem ->
                    timerService.minState = selectedItem
                }

                StyledNumberPicker(
                modifier = Modifier.offset(x = (-14).dp),
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

@Composable
fun StyledNumberPicker(
    modifier: Modifier = Modifier,
    value: Int,
    values: List<String>,
    showDivider: Boolean = false,
    name: String,
    onValueChanged: (Int) -> Unit
) {
    Box(
        modifier = modifier.size(width = 90.dp, height = 140.dp),
    ) {
        //shadows behind number picker
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .alpha(pickerVisibility),
            painter = painterResource(id = R.drawable.picker_shadow),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        if(showDivider) {
            //colon divider
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(bottom = 29.dp, end = 4.dp),
                text = ":",
                color = Black.copy(pickerVisibility),
                fontFamily = Orbitron,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        //NumberPicker
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.56f)
                .fillMaxHeight(0.80f),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    NumberPicker(context).apply {
                        minValue = 0
                        isSoundEffectsEnabled = true
                        clipToOutline = true
                        alpha = pickerVisibility
                        maxValue = values.size - 1
                        displayedValues = values.toTypedArray()
                        clipToPadding = true

                        setOnValueChangedListener { numberPicker, i, i2 ->
                            onValueChanged(numberPicker.value)
                        }
                    }
                },
                update = { picker ->
                    picker.apply {
                        alpha = pickerVisibility
                    }
                }
            )

            //shadow effect
            Canvas(
                modifier = Modifier
                    .height(45.dp)
                    .fillMaxWidth()
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
                            Color.Transparent
                        )
                    )
                )
            }

            //light effect
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(45.dp)
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

        Text(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = name,
            fontFamily = Orbitron,
            fontSize = 16.sp,
            color = Black.copy(pickerVisibility),
            fontWeight = FontWeight.SemiBold
        )
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
           PickerSection(timerService = TimerService())
        }
    }
}