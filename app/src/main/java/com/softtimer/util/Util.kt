package com.softtimer.util

import android.graphics.BlurMaskFilter
import android.graphics.drawable.shapes.ArcShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Offset.Companion.offsetFromCenter(x: Float = 0f, y: Float = 0f, center: Offset): Offset {
    return Offset(center.x + x, center.y + y)
}
