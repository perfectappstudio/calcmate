package com.perfectappstudio.scientificcalc.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.ui.theme.MintGreen
import com.perfectappstudio.scientificcalc.ui.theme.PinkAccent
import com.perfectappstudio.scientificcalc.ui.theme.PinkShadow
import com.perfectappstudio.scientificcalc.ui.theme.PurpleAccent
import com.perfectappstudio.scientificcalc.ui.theme.PurpleBright
import com.perfectappstudio.scientificcalc.ui.theme.PurpleShadow
import com.perfectappstudio.scientificcalc.ui.theme.TextPrimary

enum class CalcButtonVariant {
    Default,
    Operator,
    Equals,
    Clear,
    Scientific,
}

@Composable
fun CalcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accessibilityLabel: String = text,
    variant: CalcButtonVariant = CalcButtonVariant.Default,
    // Legacy color params for backward compatibility
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    textStyle: TextStyle = TextStyle.Default,
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "buttonScale",
    )

    // Determine effective variant from legacy colors if variant is Default
    val effectiveVariant = when {
        variant != CalcButtonVariant.Default -> variant
        backgroundColor == PurpleBright || backgroundColor == MaterialTheme.colorScheme.primary ->
            CalcButtonVariant.Equals
        backgroundColor == PinkAccent || backgroundColor == MaterialTheme.colorScheme.tertiary ->
            CalcButtonVariant.Clear
        else -> variant
    }

    val shape = when (effectiveVariant) {
        CalcButtonVariant.Scientific -> RoundedCornerShape(20.dp)
        else -> RoundedCornerShape(12.dp)
    }

    val resolvedBg = when {
        backgroundColor != Color.Unspecified -> backgroundColor
        else -> when (effectiveVariant) {
            CalcButtonVariant.Default -> Color.White.copy(alpha = 0.08f)
            CalcButtonVariant.Operator -> Color.White.copy(alpha = 0.12f)
            CalcButtonVariant.Equals -> PurpleBright
            CalcButtonVariant.Clear -> PinkAccent
            CalcButtonVariant.Scientific -> Color.White.copy(alpha = 0.06f)
        }
    }

    val resolvedContent = when {
        contentColor != Color.Unspecified -> contentColor
        else -> when (effectiveVariant) {
            CalcButtonVariant.Default -> TextPrimary
            CalcButtonVariant.Operator -> PurpleAccent
            CalcButtonVariant.Equals -> TextPrimary
            CalcButtonVariant.Clear -> TextPrimary
            CalcButtonVariant.Scientific -> MintGreen
        }
    }

    val resolvedTextStyle = when {
        textStyle != TextStyle.Default -> textStyle
        effectiveVariant == CalcButtonVariant.Scientific -> MaterialTheme.typography.labelLarge
        else -> MaterialTheme.typography.bodyLarge
    }

    val shadowModifier = when (effectiveVariant) {
        CalcButtonVariant.Equals -> Modifier.neoBrutalistShadow(
            shadowColor = PurpleShadow,
            offsetX = 3.dp,
            offsetY = 3.dp,
        )
        CalcButtonVariant.Clear -> Modifier.neoBrutalistShadow(
            shadowColor = PinkShadow,
            offsetX = 3.dp,
            offsetY = 3.dp,
        )
        else -> Modifier
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 52.dp, minHeight = 52.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .semantics { contentDescription = accessibilityLabel }
            .then(shadowModifier)
            .clip(shape)
            .background(resolvedBg)
            .border(
                width = 1.dp,
                color = when (effectiveVariant) {
                    CalcButtonVariant.Equals, CalcButtonVariant.Clear -> Color.Transparent
                    else -> Color.White.copy(alpha = 0.1f)
                },
                shape = shape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = resolvedTextStyle,
            color = resolvedContent,
        )
    }
}
