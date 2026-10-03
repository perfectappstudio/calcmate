package com.perfectappstudio.scientificcalc.feature.calculator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ExpressionDisplay(
    expression: String,
    result: String,
    error: String?,
    hasEvaluated: Boolean,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val expressionScrollState = rememberScrollState()

    // Auto-scroll to end when expression changes
    LaunchedEffect(expression) {
        expressionScrollState.animateScrollTo(expressionScrollState.maxValue)
    }

    val expressionColor by animateColorAsState(
        targetValue = if (hasEvaluated) {
            Color(0xFF4A605A)
        } else {
            Color(0xFF152C25)
        },
        label = "expressionColor",
    )

    val resultColor by animateColorAsState(
        targetValue = if (hasEvaluated) {
            Color(0xFF152C25)
        } else {
            Color(0xFF4A605A)
        },
        label = "resultColor",
    )

    // Animate result alpha so new values fade in
    val resultAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 250),
        label = "resultAlpha",
    )

    val expressionStyle = if (hasEvaluated || compact) {
        MaterialTheme.typography.bodyLarge
    } else {
        MaterialTheme.typography.headlineMedium
    }

    val resultStyle = if (hasEvaluated && !compact) {
        MaterialTheme.typography.displayLarge
    } else {
        MaterialTheme.typography.headlineLarge
    }

    // Build accessible descriptions
    val expressionDesc = if (expression.isNotEmpty()) "Expression: $expression" else "Empty expression"
    val resultDesc = when {
        error != null -> "Error: $error"
        result.isNotEmpty() -> "Result: $result"
        else -> "No result"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFDBE9E1))
            .padding(horizontal = 20.dp, vertical = if (compact) 8.dp else 16.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$expressionDesc. $resultDesc"
            },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        // Expression line
        Text(
            text = expression.ifEmpty { "0" },
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(expressionScrollState)
                .animateContentSize(animationSpec = tween(200))
                .clearAndSetSemantics { },
            textAlign = TextAlign.End,
            style = expressionStyle,
            color = expressionColor,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )

        // Result / error line
        Text(
            text = error ?: result.ifEmpty { " " },
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .alpha(resultAlpha)
                .animateContentSize(animationSpec = tween(200))
                .clearAndSetSemantics { },
            textAlign = TextAlign.End,
            style = if (error != null) MaterialTheme.typography.bodyMedium else resultStyle,
            color = if (error != null) {
                Color(0xFFEF4444) // Red for errors
            } else {
                resultColor
            },
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )

        // Hidden live region for TalkBack to announce result changes
        Text(
            text = "",
            modifier = Modifier.semantics {
                contentDescription = resultDesc
                liveRegion = LiveRegionMode.Polite
            },
        )
    }
}
