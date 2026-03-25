package com.calcmate.scientificcalculator.feature.calculator

import androidx.compose.animation.animateColorAsState
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        // Expression line
        Text(
            text = expression.ifEmpty { " " },
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(expressionScrollState),
            textAlign = TextAlign.End,
            style = expressionStyle,
            color = expressionColor,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )

        // Result / error line
        Text(
            text = error ?: result.ifEmpty { " " },
            modifier = Modifier.fillMaxWidth(),
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
    }
}
