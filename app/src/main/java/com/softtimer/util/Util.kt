package com.softtimer.util

import android.graphics.BlurMaskFilter
import android.graphics.drawable.shapes.ArcShape
import android.util.Log
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Offset.Companion.offsetFromCenter(x: Float = 0f, y: Float = 0f, center: Offset): Offset {
    return Offset(center.x + x, center.y + y)
}

fun formatTime(
    isTimeExpired: Boolean = false,
    seconds: String,
    minutes: String,
    hours: String,
    millis: String
): String {
    return if(isTimeExpired) "-$minutes:$seconds:$millis" else "$hours:$minutes.$seconds"
}

fun Int.pad(length: Int = 2, padChar: Char = '0'): String {
    return this.toString().padStart(length, padChar)
}

fun Int.absPad(length: Int = 2, padChar: Char = '0'): String {
    return abs(this).pad()
}

fun Int.formatMillis(): String {
    return String.format("%02d", this / 10000000)
}


fun getNumbersWithPad(range: IntRange): List<String> {
    val timeNumbers = mutableListOf("00")
    range.map { it }.forEach {
        timeNumbers.add(it.pad())
    }
    return timeNumbers
}

fun getDurationInSec(h: Int, min: Int, s: Int): Duration {
    val hInSec = h * 60 * 60
    val minInSec = min * 60
    return (hInSec + minInSec + s).seconds
}

@Composable
fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }
