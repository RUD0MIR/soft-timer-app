package com.softtimer.util

import android.graphics.BlurMaskFilter
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

fun Modifier.shadow(
    shape: Shape = RectangleShape,
    color: Color = Color.Black,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0f.dp,
    modifier: Modifier = Modifier
) = this.then(
    modifier.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel)
            val bottomPixel = (this.size.height + spreadPixel)

            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }

            frameworkPaint.color = color.toArgb()

            when(shape) {
                RectangleShape -> {
                    it.drawRoundRect(
                        left = leftPixel,
                        top = topPixel,
                        right = rightPixel,
                        bottom = bottomPixel,
                        radiusX = borderRadius.toPx(),
                        radiusY = borderRadius.toPx(),
                        paint
                    )
                }
                CircleShape -> {
                    it.drawCircle(
                        center = Offset.offsetFromCenter(
                            x = offsetX.toPx(),
                            y = offsetY.toPx(),
                            center = center
                        ),
                        radius = borderRadius.toPx() / 2,
                        paint
                    )
                }
            }
        }
    }
)