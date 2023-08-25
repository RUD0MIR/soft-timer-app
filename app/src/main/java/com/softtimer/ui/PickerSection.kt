package com.softtimer.ui

import android.util.Log
import android.widget.NumberPicker
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.softtimer.TimerViewModel
import com.softtimer.service.TimerService
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.Black
import com.softtimer.ui.theme.FaintLight
import com.softtimer.ui.theme.FaintLight1
import com.softtimer.ui.theme.FaintShadow
import com.softtimer.ui.theme.MID_ANIMATION_DELAY
import com.softtimer.ui.theme.MID_ANIMATION_DURATION
import com.softtimer.ui.theme.Orbitron
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.getNumbersWithPad
import kotlinx.coroutines.delay

private const val TAG = "PickerSection"

@Composable
fun PickerSection(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    hValue: Int,
    minValue: Int,
    sValue: Int,
    pickerVisibilityValue: Float,
    onPickerVisibilityValueChanged: (Float) -> Unit,
    isVisible: Boolean,
    onVisibilityChanged: (Boolean) -> Unit,
    onHPickerStateChanged: (Int) -> Unit,
    onMinPickerStateChanged: (Int) -> Unit,
    onSecPickerStateChanged: (Int) -> Unit,
) {


    val pickerVisibility by animateFloatAsState(
        targetValue = pickerVisibilityValue,
        animationSpec = tween(
            durationMillis = MID_ANIMATION_DURATION,
            easing = LinearEasing
        )
    )

    LaunchedEffect(key1 = timerState) {
        when (timerState) {
            TimerState.Running -> {
                onPickerVisibilityValueChanged(0f)
                delay(MID_ANIMATION_DELAY)
                onVisibilityChanged(false)
            }

            TimerState.Idle -> {
                onVisibilityChanged(true)
                onPickerVisibilityValueChanged(1f)
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
                    modifier = Modifier
                        .offset(x = 14.dp)
                        .alpha(pickerVisibility),
                    range = getNumbersWithPad(1..24),
                    showDivider = true,
                    pickerValue = hValue,
                    name = "h"
                ) { selectedHour ->
                    onHPickerStateChanged(selectedHour)
                }

                StyledNumberPicker(
                    modifier = Modifier.alpha(pickerVisibility),
                    range = getNumbersWithPad(1..59),
                    name = "min",
                    pickerValue = minValue,
                    showDivider = true,
                ) { selectedMin ->
                    onMinPickerStateChanged(selectedMin)

                }

                StyledNumberPicker(
                    modifier = Modifier
                        .offset(x = (-14).dp)
                        .alpha(pickerVisibility),
                    range = getNumbersWithPad(1..59),
                    pickerValue = sValue,
                    name = "s"
                ) { selectedSec ->
                    onSecPickerStateChanged(selectedSec)

                }
            }
        }
    }
}

@Composable
fun StyledNumberPicker(
    modifier: Modifier = Modifier,
    range: List<String>,
    showDivider: Boolean = false,
    name: String,
    pickerValue: Int,
    onValueChanged: (Int) -> Unit
) {
    Box(
        modifier = modifier.size(width = 90.dp, height = 140.dp),
    ) {
        //shadows behind number picker
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            painter = painterResource(id = R.drawable.picker_shadow),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        if (showDivider) {
            //colon divider
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(bottom = 29.dp, end = 4.dp),
                text = ":",
                color = Black,
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
                        maxValue = range.size - 1
                        displayedValues = range.toTypedArray()
                        clipToPadding = true

                        setOnValueChangedListener { numberPicker, i, i2 ->
                                onValueChanged(numberPicker.value)
                        }
                    }
                },
                update = {
                        it.value = pickerValue
                    Log.d(TAG, "${it.value}")
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
                            FaintShadow,
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
                            FaintLight1,
                            FaintLight,
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
            color = Black,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TestPreview() {
    SoftTImerTheme {
    }
}