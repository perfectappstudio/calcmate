package com.calcmate.scientificcalculator.feature.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calcmate.scientificcalculator.core.model.AngleUnit
import com.calcmate.scientificcalculator.core.model.CalculatorAction
import com.calcmate.scientificcalculator.core.model.CalculatorState
import com.calcmate.scientificcalculator.ui.components.CalcButton

private val ButtonSpacing = 4.dp

@Composable
fun ScientificKeypadLand(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Left half: scientific functions
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ButtonSpacing),
        ) {
            LandToggleChips(state = state, onAction = onAction)
            LandSciRow1(state = state, onAction = onAction)
            LandSciRow2(state = state, onAction = onAction)
            LandSciRow3(onAction = onAction)
            LandSciRow4(onAction = onAction)
        }

        // Right half: number pad + operators
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ButtonSpacing),
        ) {
            LandMainRow1(onAction = onAction)
            LandMainRow2(onAction = onAction)
            LandMainRow3(onAction = onAction)
            LandMainRow4(onAction = onAction)
            LandMainRow5(onAction = onAction)
            // Equals button
            CalcButton(
                text = "=",
                onClick = { onAction(CalculatorAction.Equals) },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                textStyle = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

// --- Toggle chips for landscape ---

@Composable
private fun LandToggleChips(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FilterChip(
            selected = state.angleUnit == AngleUnit.RADIAN,
            onClick = { onAction(CalculatorAction.ToggleAngleUnit) },
            label = {
                Text(if (state.angleUnit == AngleUnit.RADIAN) "RAD" else "DEG")
            },
        )
        FilterChip(
            selected = state.isInverse,
            onClick = { onAction(CalculatorAction.ToggleInverse) },
            label = { Text("INV") },
        )
        FilterChip(
            selected = state.isHyperbolic,
            onClick = { onAction(CalculatorAction.ToggleHyperbolic) },
            label = { Text("HYP") },
        )
    }
}

// --- Scientific rows (landscape left half) ---

@Composable
private fun LandSciRow1(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
) {
    val labels = buildLandTrigLabels(state.isInverse, state.isHyperbolic)
    LandRow {
        LandSciFuncButton(labels[0], "sin", onAction)
        LandSciFuncButton(labels[1], "cos", onAction)
        LandSciFuncButton(labels[2], "tan", onAction)
    }
}

@Composable
private fun LandSciRow2(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
) {
    LandRow {
        LandSciFuncButton("ln", "ln", onAction)
        LandSciFuncButton("log", "log", onAction)
        LandSciFuncButton("\u221A", "sqrt", onAction)
    }
}

@Composable
private fun LandSciRow3(onAction: (CalculatorAction) -> Unit) {
    LandRow {
        LandSciButton("\u03C0") { onAction(CalculatorAction.Constant("\u03C0")) }
        LandSciButton("e") { onAction(CalculatorAction.Constant("e")) }
        LandSciButton("^") { onAction(CalculatorAction.Operator("^")) }
    }
}

@Composable
private fun LandSciRow4(onAction: (CalculatorAction) -> Unit) {
    LandRow {
        LandSciButton("!") { onAction(CalculatorAction.Operator("!")) }
        LandSciButton("(") { onAction(CalculatorAction.OpenParen) }
        LandSciButton(")") { onAction(CalculatorAction.CloseParen) }
    }
}

// --- Main rows (landscape right half) ---

@Composable
private fun LandMainRow1(onAction: (CalculatorAction) -> Unit) {
    LandRow {
        CalcButton(
            text = "C",
            onClick = { onAction(CalculatorAction.Clear) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
        )
        CalcButton(
            text = "\u232B",
            onClick = { onAction(CalculatorAction.Backspace) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        LandOpButton("%", onAction)
        LandOpButton("\u00F7", onAction)
    }
}

@Composable
private fun LandMainRow2(onAction: (CalculatorAction) -> Unit) {
    LandRow {
        LandNumButton("7", onAction)
        LandNumButton("8", onAction)
        LandNumButton("9", onAction)
        LandOpButton("\u00D7", onAction)
    }
}

@Composable
private fun LandMainRow3(onAction: (CalculatorAction) -> Unit) {
    LandRow {
        LandNumButton("4", onAction)
        LandNumButton("5", onAction)
        LandNumButton("6", onAction)
        LandOpButton("\u2212", onAction)
    }
}

@Composable
private fun LandMainRow4(onAction: (CalculatorAction) -> Unit) {
    LandRow {
        LandNumButton("1", onAction)
        LandNumButton("2", onAction)
        LandNumButton("3", onAction)
        LandOpButton("+", onAction)
    }
}

@Composable
private fun LandMainRow5(onAction: (CalculatorAction) -> Unit) {
    LandRow {
        CalcButton(
            text = "+/\u2212",
            onClick = { onAction(CalculatorAction.ToggleSign) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        LandNumButton("0", onAction)
        CalcButton(
            text = ".",
            onClick = { onAction(CalculatorAction.Decimal) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        // Empty weight spacer so columns line up -- use the percent key space
        CalcButton(
            text = "",
            onClick = {},
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// --- Helpers ---

@Composable
private fun LandRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonSpacing),
        content = content,
    )
}

@Composable
private fun RowScope.LandNumButton(digit: String, onAction: (CalculatorAction) -> Unit) {
    CalcButton(
        text = digit,
        onClick = { onAction(CalculatorAction.Digit(digit)) },
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun RowScope.LandOpButton(symbol: String, onAction: (CalculatorAction) -> Unit) {
    CalcButton(
        text = symbol,
        onClick = { onAction(CalculatorAction.Operator(symbol)) },
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun RowScope.LandSciFuncButton(
    label: String,
    baseName: String,
    onAction: (CalculatorAction) -> Unit,
) {
    CalcButton(
        text = label,
        onClick = { onAction(CalculatorAction.Function(baseName)) },
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun RowScope.LandSciButton(label: String, onClick: () -> Unit) {
    CalcButton(
        text = label,
        onClick = onClick,
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

private fun buildLandTrigLabels(isInverse: Boolean, isHyperbolic: Boolean): List<String> {
    val base = listOf("sin", "cos", "tan")
    return base.map { name ->
        when {
            isInverse && isHyperbolic -> "a${name}h"
            isInverse -> "a$name"
            isHyperbolic -> "${name}h"
            else -> name
        }
    }
}
