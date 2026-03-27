package com.perfectappstudio.scientificcalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.ui.theme.DeepSpaceBottom
import com.perfectappstudio.scientificcalc.ui.theme.DeepSpaceTop

fun Modifier.glassPanel(
    borderRadius: Dp = 16.dp,
    opacity: Float = 0.06f,
): Modifier = this
    .clip(RoundedCornerShape(borderRadius))
    .background(Color.White.copy(alpha = opacity))
    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(borderRadius))

fun Modifier.neoBrutalistShadow(
    shadowColor: Color,
    offsetX: Dp = 3.dp,
    offsetY: Dp = 3.dp,
): Modifier = this
    .drawBehind {
        drawRoundRect(
            color = shadowColor,
            topLeft = Offset(offsetX.toPx(), offsetY.toPx()),
            size = size,
            cornerRadius = CornerRadius(12.dp.toPx()),
        )
    }

fun Modifier.deepSpaceBackground(): Modifier = this
    .background(
        Brush.verticalGradient(
            colors = listOf(DeepSpaceTop, DeepSpaceBottom),
        ),
    )
