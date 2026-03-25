package com.calcmate.scientificcalculator.feature.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calcmate.scientificcalculator.core.model.AngleUnit
import com.calcmate.scientificcalculator.core.model.CalculatorAction
import com.calcmate.scientificcalculator.core.model.CalculatorState
import com.calcmate.scientificcalculator.ui.components.CalcButton

private val ButtonSpacing = 4.dp
private val SectionSpacing = 6.dp

@Composable
fun ScientificKeypad(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(ButtonSpacing),
    ) {
        // Expandable scientific functions
        AnimatedVisibility(
            visible = state.isScientificExpanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(ButtonSpacing)) {
                ScientificRow1(state = state, onAction = onAction)
                ScientificRow2(onAction = onAction)
            }
        }

        // Toggle chips row: RAD/DEG, INV, HYP
        ToggleChipsRow(state = state, onAction = onAction)

        // Main keypad grid
        MainKeypadRow1(onAction = onAction)
        MainKeypadRow2(onAction = onAction)
        MainKeypadRow3(onAction = onAction)
        MainKeypadRow4(onAction = onAction)
        MainKeypadRow5(onAction = onAction)

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

// --- Scientific function rows ---

@Composable
private fun ScientificRow1(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
) {
    val labels = buildTrigLabels(state.isInverse, state.isHyperbolic)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonSpacing),
    ) {
        SciFuncButton(labels[0], "sin", onAction)
        SciFuncButton(labels[1], "cos", onAction)
        SciFuncButton(labels[2], "tan", onAction)
        SciFuncButton("ln", "ln", onAction)
        SciFuncButton("log", "log", onAction)
        SciFuncButton("\u221A", "sqrt", onAction) // sqrt symbol
    }
}

@Composable
private fun ScientificRow2(onAction: (CalculatorAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonSpacing),
    ) {
        SciButton("\u03C0") { onAction(CalculatorAction.Constant("\u03C0")) }
        SciButton("e") { onAction(CalculatorAction.Constant("e")) }
        SciButton("^") { onAction(CalculatorAction.Operator("^")) }
        SciButton("!") { onAction(CalculatorAction.Operator("!")) }
        SciButton("(") { onAction(CalculatorAction.OpenParen) }
        SciButton(")") { onAction(CalculatorAction.CloseParen) }
    }
}

// --- Toggle chips ---

@Composable
private fun ToggleChipsRow(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
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

// --- Main keypad rows ---

@Composable
private fun MainKeypadRow1(onAction: (CalculatorAction) -> Unit) {
    KeypadRow {
        CalcButton(
            text = "C",
            onClick = { onAction(CalculatorAction.Clear) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
        )
        CalcButton(
            text = "(",
            onClick = { onAction(CalculatorAction.OpenParen) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        CalcButton(
            text = ")",
            onClick = { onAction(CalculatorAction.CloseParen) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        CalcButton(
            text = "\u232B", // backspace symbol
            onClick = { onAction(CalculatorAction.Backspace) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun MainKeypadRow2(onAction: (CalculatorAction) -> Unit) {
    KeypadRow {
        NumButton("7", onAction)
        NumButton("8", onAction)
        NumButton("9", onAction)
        OpButton("\u00F7", onAction) // division sign
    }
}

@Composable
private fun MainKeypadRow3(onAction: (CalculatorAction) -> Unit) {
    KeypadRow {
        NumButton("4", onAction)
        NumButton("5", onAction)
        NumButton("6", onAction)
        OpButton("\u00D7", onAction) // multiplication sign
    }
}

@Composable
private fun MainKeypadRow4(onAction: (CalculatorAction) -> Unit) {
    KeypadRow {
        NumButton("1", onAction)
        NumButton("2", onAction)
        NumButton("3", onAction)
        OpButton("\u2212", onAction) // minus sign
    }
}

@Composable
private fun MainKeypadRow5(onAction: (CalculatorAction) -> Unit) {
    KeypadRow {
        CalcButton(
            text = "+/\u2212",
            onClick = { onAction(CalculatorAction.ToggleSign) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        NumButton("0", onAction)
        CalcButton(
            text = ".",
            onClick = { onAction(CalculatorAction.Decimal) },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        OpButton("+", onAction)
    }
}

// --- Reusable row / button helpers ---

@Composable
private fun KeypadRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonSpacing),
        content = content,
    )
}

@Composable
private fun RowScope.NumButton(digit: String, onAction: (CalculatorAction) -> Unit) {
    CalcButton(
        text = digit,
        onClick = { onAction(CalculatorAction.Digit(digit)) },
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun RowScope.OpButton(symbol: String, onAction: (CalculatorAction) -> Unit) {
    CalcButton(
        text = symbol,
        onClick = { onAction(CalculatorAction.Operator(symbol)) },
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun RowScope.SciFuncButton(
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
private fun RowScope.SciButton(label: String, onClick: () -> Unit) {
    CalcButton(
        text = label,
        onClick = onClick,
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

private fun buildTrigLabels(isInverse: Boolean, isHyperbolic: Boolean): List<String> {
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
