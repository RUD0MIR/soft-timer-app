package com.softtimer.ui.theme

import androidx.compose.ui.graphics.Color

val ButtonText = if(isLightTheme.value) Color(0xFF323743) else Color(0xFFB1ADA9)
val FaintShadow = if(isLightTheme.value) Color(0x48CFCFCF) else Color(0x78000000)
val FaintShadow1 = if(isLightTheme.value) Color(0x196F6F6F) else   Color(0x19000000)
val FaintLight1 = if(isLightTheme.value) Color(0x88F5F5F5) else Color(0x883E3E3E)
val FaintLight = if(isLightTheme.value) Color(0x4FF5F5F5) else Color(0x4F3E3E3E)

val LightGlow = if(isLightTheme.value) Color(0xFFC3D0F2) else Color(0xFFE6CA7D)
val Glow = if(isLightTheme.value) Color(0xFF305FD6) else Color(0xFFD6A627)
val MidGlow = if(isLightTheme.value) Color(0xFF6F8EDF) else Color(0xFFDEB852)
val GlowLight = if(isLightTheme.value) Color(0xFFC0CDF1) else Color(0xFFE6CA7D)
val GlowFlash = if(isLightTheme.value) Color(0xFFCFDAF4) else Color(0xFFEFDBA9)