package com.softtimer.ui

import android.os.Build
import android.widget.NumberPicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.softtimer.R
import com.softtimer.service.TimerState
import com.softtimer.ui.theme.Orbitron
import com.softtimer.ui.theme.SoftTImerTheme
import com.softtimer.util.Constants.MID_ANIMATION_DELAY
import com.softtimer.util.Constants.MID_ANIMATION_DURATION
import com.softtimer.util.getNumbersWithPad
import kotlinx.coroutines.delay

private const val TAG = "PickerSection"

@Composable
fun PickerSection(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    isDarkTheme: Boolean,
    hValue: Int,
    minValue: Int,
    sValue: Int,
    onHPickerStateChanged: (Int) -> Unit,
    onMinPickerStateChanged: (Int) -> Unit,
    onSecPickerStateChanged: (Int) -> Unit,
) {
    var isPickerVisible by rememberSaveable {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = timerState) {
        when (timerState) {
            TimerState.Running -> {
                isPickerVisible = false
                delay(MID_ANIMATION_DELAY)
            }

            TimerState.Idle -> {
                isPickerVisible = true
            }

            else -> {}
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(139.dp)
    ) {
        AnimatedVisibility(
            isPickerVisible,
            enter = fadeIn(animationSpec = tween(MID_ANIMATION_DURATION)),
            exit = fadeOut(animationSpec = tween(MID_ANIMATION_DURATION))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                StyledNumberPicker(
                    modifier = Modifier
                        .offset(x = 14.dp),
                    isDarkTheme = isDarkTheme,
                    range = getNumbersWithPad(1..24),
                    showDivider = true,
                    pickerValue = hValue,
                    name = "h"
                ) { selectedHour ->
                    onHPickerStateChanged(selectedHour)
                }

                StyledNumberPicker(
                    isDarkTheme = isDarkTheme,
                    range = getNumbersWithPad(1..59),
                    name = "min",
                    pickerValue = minValue,
                    showDivider = true,
                ) { selectedMin ->
                    onMinPickerStateChanged(selectedMin)

                }

                StyledNumberPicker(
                    modifier = Modifier
                        .offset(x = (-14).dp),
                    isDarkTheme = isDarkTheme,
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
    isDarkTheme: Boolean,
    range: List<String>,
    showDivider: Boolean = false,
    name: String,
    pickerValue: Int,
    onValueChanged: (Int) -> Unit
) {
    Box(
        modifier = modifier.size(width = 90.dp, height = 140.dp),
    ) {
        val onSurface =  MaterialTheme.colorScheme.onSurface
        val surfaceBright =  MaterialTheme.colorScheme.surfaceBright

        //shadows behind number picker
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            painter = painterResource(id = if (isDarkTheme) R.drawable.dark_picker_shadow else R.drawable.picker_shadow),
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
                color = MaterialTheme.colorScheme.onSurface,
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        it.textColor = onSurface.toArgb()

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
                            onSurface.copy(alpha = 0.1f),
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
                            surfaceBright,
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
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TestPreview() {
    SoftTImerTheme(dynamicColor = false) {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(32.dp)
        ) {
            PickerSection(
                timerState = TimerState.Idle,
                isDarkTheme = false,
                hValue = 0,
                minValue = 0,
                sValue = 0,
                onHPickerStateChanged = {},
                onMinPickerStateChanged = {}
            ) {

            }
        }
    }
}