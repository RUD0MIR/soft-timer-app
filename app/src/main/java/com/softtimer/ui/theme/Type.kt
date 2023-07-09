package com.softtimer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.sp
import com.softtimer.R

val Orbitron = FontFamily(
    listOf(
        Font(R.font.orbitron_regular, FontWeight.Normal),
        Font(R.font.orbitron_medium, FontWeight.Medium),
        Font(R.font.orbitron_semi_bold, FontWeight.SemiBold),
        Font(R.font.orbitron_bold, FontWeight.Bold),
        Font(R.font.orbitron_extra_bold, FontWeight.ExtraBold),
        Font(R.font.orbitron_black, FontWeight.Black),
    )
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)