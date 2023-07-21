package com.softtimer.ui

//MidCircle(diameter = 170f * sizeModifier)//170

//TimerDividers(diameter = 230f * sizeModifier)//230

//BottomCircle(diameter = 210f * sizeModifier)//210

//@Composable
//fun TimerDividers(diameter: Float) {
//    var circleCenter by remember {
//        mutableStateOf(Offset.Zero)
//    }
//
//    Canvas(modifier = Modifier.fillMaxSize()) {
//        circleCenter = Offset(x = size.width / 2f, y = size.height / 2f)
//        val lineLength = 15f * sizeModifier
//        val dividersCount = 96
//
//        for (i in 0 until dividersCount) {
//            val angleInDegrees = i * 360f / dividersCount
//            val angleInRad = angleInDegrees * PI / 180f + PI / 2f
//            val lineThickness = 0.8f * sizeModifier
//
//            val start = Offset(
//                x = (diameter * cos(angleInRad) + circleCenter.x).toFloat(),
//                y = (diameter * sin(angleInRad) + circleCenter.y).toFloat()
//            )
//
//            val end = Offset(
//                x = (diameter * cos(angleInRad) + circleCenter.x).toFloat(),
//                y = (diameter * sin(angleInRad) + lineLength + circleCenter.y).toFloat()
//            )
//            rotate(
//                angleInDegrees + 180,
//                pivot = start
//            ) {
//                drawLine(
//                    color = Color(0xFFB8B8B8),
//                    start = start,
//                    end = end,
//                    strokeWidth = lineThickness.dp.toPx()
//                )
//            }
//        }
//    }
//}

//Timer numbers
//    Canvas(
//        modifier = Modifier
//            .size(size.dp)//95
//            .circleShadow(
//                color = Light,
//                radius = size.dp,
//                blurRadius = (15f * sizeModifier).dp,//15
//                offsetX = -(4f * sizeModifier).dp,//-4
//                offsetY = -(2f * sizeModifier).dp//-2
//            )
//            .circleShadow(
//                color = Shadow,
//                radius = (95f * sizeModifier).dp,//95
//                blurRadius = (15f * sizeModifier).dp,//15
//                offsetX = (12f * sizeModifier).dp,//12
//                offsetY = (16f * sizeModifier).dp//16
//            ),
//        onDraw = {
//            drawCircle(color = White1)
//        }
//    )

//@Composable
//fun MidCircle(diameter: Float) {
//    Canvas(
//        modifier = Modifier
//            .size(diameter.dp)//170
//            .circleShadow(
//                color = DarkShadow,
//                radius = (178f * sizeModifier).dp,//178
//                blurRadius = (7f * sizeModifier).dp,//7
//                offsetX = (6f * sizeModifier).dp,//6
//                offsetY = (10f * sizeModifier).dp//10
//            ),
//        onDraw = {
//            //ambient light & shadow
//            drawCircle(
//                radius = (173f * sizeModifier).dp.toPx() / 2,
//                brush = Brush.verticalGradient(
//                    colorStops = arrayOf(
//                        Pair(0.2f, Light),
//                        Pair(1f, Color(0xFF878787))
//                    )
//                )
//            )
//
//            //circle itself
//            drawCircle(
//                radius = diameter.dp.toPx() / 2,
//                brush = Brush.linearGradient(
//                    colorStops = arrayOf(
//                        Pair(0.4f, Color(0xFFE0DEDE)),
//                        Pair(1f, Color(0xFFCFCFCF))
//                    )
//                )
//            )
//        }
//    )
//}

//@Composable
//fun BottomCircle(diameter: Float) {
//    Canvas(
//        modifier = Modifier
//            .circleShadow(
//                color = Shadow,
//                radius = (216f * sizeModifier).dp,//216
//                blurRadius = (10f * sizeModifier).dp,//10
//                offsetX = (5f * sizeModifier).dp,//5
//                offsetY = (5f * sizeModifier).dp,//5
//            )
//            .circleShadow(
//                color = Light,
//                radius = diameter.dp,//210
//                blurRadius = (10f * sizeModifier).dp,//10
//                offsetY = -(5f * sizeModifier).dp,//-5
//            )
//            .size(diameter.dp)
//            .clip(CircleShape),
//        onDraw = {
//            drawCircle(
//                Brush.radialGradient(
//                    center = Offset
//                        .offsetFromCenter(
//                            x = 20f * sizeModifier,//20
//                            y = 70f * sizeModifier,//70
//                            center = center
//                        ),
//                    colorStops = arrayOf(
//                        Pair(0.8f, Color(0xFFE2E0E0)),
//                        Pair(1f, Color(0xFFC9C8C8))
//                    ),
//                    radius = 370f * sizeModifier//370
//                )
//            )
//        }
//    )
//}

////            Picker(
////                modifier = Modifier
////                    .size(width = 50.dp, height = 115.dp)
////                    .align(Alignment.Center),
////                value = value,
////                items = values,
////                visibleItemsCount = 3,
////                textModifier = Modifier.padding(vertical = 8.dp),
////                textStyle = TextStyle(
////                    fontSize = 20.sp,
////                    fontFamily = Orbitron,
////                    color = Black.copy(visibility)
////                )
////            ) { selectedItem, listState ->
////                onValueChanged(selectedItem, listState)
////            }


