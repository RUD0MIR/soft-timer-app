package com.softtimer.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.DefaultStrokeLineMiter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softtimer.ui.theme.SoftTImerTheme

//TODO: customize appearance
@Composable
fun CustomProgressIndicator(
    modifier: Modifier,
    progress: () -> Float,
    startAngle: Float = 270f,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
) {
    val primary = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright

    val coercedProgress = { progress().coerceIn(0f, 1f) }
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = strokeCap)
    }
    Canvas(
        modifier
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo = ProgressBarRangeInfo(coercedProgress(), 0f..1f)
            }
    ) {
        val sweep = coercedProgress() * 360f

        drawCustomCircularIndicator(
            startAngle = startAngle,
            sweep = sweep,
            stroke = stroke,
            colorPrimary = primary,
            colorSurface = surface,
            colorSurfaceBright = surfaceBright
        )
    }
}

private fun DrawScope.drawCustomCircularIndicator(
    startAngle: Float,
    sweep: Float,
    stroke: Stroke,
    colorPrimary: Color,
    colorSurface: Color,
    colorSurfaceBright: Color,
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val arcDimen = size.width - 2
    val fixedStartAngel = startAngle + 90f

    rotate(degrees = startAngle - 360f) {
        //progress bar
    drawArc(
        startAngle = fixedStartAngel, //"+90" fixing rendering bug
        sweepAngle = -sweep,
        useCenter = false,
        size = Size(arcDimen, arcDimen),
        brush = Brush.sweepGradient(
            colorStops = listOf(
                0f to colorSurface,
                sweep / 360 to colorPrimary,
            ).toTypedArray()
        ),
        style = stroke
    )
//
//    //flash effect
//    drawArc(
//        startAngle = startAngle,
//        sweepAngle = -sweep,
//        useCenter = false,
//        brush = Brush.sweepGradient(
//            colors =
//            listOf(
//                Color.Transparent,
//                colorSurfaceBright.copy(alpha = 0.5f),
//                colorSurfaceBright,
//                colorSurfaceBright.copy(alpha = 0.5f),
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//                Color.Transparent,
//            ),
//        ),
//        style = stroke
//    )
//
////    line around progress bar
        val arcSize = size.width + (stroke.width * 0.9f)
        val arcOffset = -stroke.width * 0.45f
        drawArc(
            startAngle = fixedStartAngel,
            sweepAngle = -sweep,
            topLeft = Offset(arcOffset, arcOffset),
            size = Size(arcSize, arcSize),
            style = Stroke(width = stroke.width * 0.1f, cap = stroke.cap),
            useCenter = false,
            brush = Brush.sweepGradient(
                colorStops = listOf(
                    0.1f to Color.Transparent,
                    0.5f to colorPrimary ,
                ).toTypedArray()
            )
        )
    }
}

@Preview
@Composable
private fun IndicatorPreview() {
    SoftTImerTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Box(modifier = Modifier.padding(32.dp).fillMaxSize(), contentAlignment = Alignment.Center) {
                CustomProgressIndicator(
                    modifier = Modifier.size(220.dp),
                    progress = { 1f },
                    strokeWidth = 30.dp
                )
            }
        }
    }
}