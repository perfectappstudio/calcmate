package com.calcmate.scientificcalculator.feature.calculator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
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
) {
    val expressionScrollState = rememberScrollState()

    // Auto-scroll to end when expression changes
    LaunchedEffect(expression) {
        expressionScrollState.animateScrollTo(expressionScrollState.maxValue)
    }

    val expressionColor by animateColorAsState(
        targetValue = if (hasEvaluated) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "expressionColor",
    )

    val resultColor by animateColorAsState(
        targetValue = if (hasEvaluated) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "resultColor",
    )

    // Animate result alpha so new values fade in
    val resultAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 250),
        label = "resultAlpha",
    )

    val expressionStyle = if (hasEvaluated) {
        MaterialTheme.typography.headlineSmall
    } else {
        MaterialTheme.typography.headlineMedium
    }

    val resultStyle = if (hasEvaluated) {
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$expressionDesc. $resultDesc"
            },
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        // Expression line
        Text(
            text = expression.ifEmpty { " " },
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
                .alpha(resultAlpha)
                .animateContentSize(animationSpec = tween(200))
                .clearAndSetSemantics { },
            textAlign = TextAlign.End,
            style = resultStyle,
            color = if (error != null) {
                MaterialTheme.colorScheme.error
            } else {
                resultColor
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
