package com.perfectappstudio.scientificcalc.feature.basen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.core.parser.NumberBase
import com.perfectappstudio.scientificcalc.ui.components.CalcButton

private val ButtonSpacing = 4.dp

@Composable
fun BaseNKeypad(
    currentBase: NumberBase,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onEquals: () -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onOpenParen: () -> Unit,
    onCloseParen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(ButtonSpacing),
    ) {
        // Logic operators row
        LogicOpsRow(onOperator = onOperator)

        // Hex letters row (only in HEX mode)
        if (currentBase == NumberBase.HEX) {
            HexLettersRow(onDigit = onDigit)
        }

        // Control row: C, (, ), DEL
        ControlRow(
            onClear = onClear,
            onDelete = onDelete,
            onOpenParen = onOpenParen,
            onCloseParen = onCloseParen,
        )

        // Number rows adapt per base
        NumberRows(
            currentBase = currentBase,
            onDigit = onDigit,
            onOperator = onOperator,
        )

        // Bottom row: 0, +, =
        BottomRow(
            currentBase = currentBase,
            onDigit = onDigit,
            onOperator = onOperator,
            onEquals = onEquals,
        )
    }
}

@Composable
private fun LogicOpsRow(onOperator: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonSpacing),
    ) {
        LogicButton("AND", onOperator)
        LogicButton("OR", onOperator)
        LogicButton("XOR", onOperator)
        LogicButton("XNOR", onOperator)
        LogicButton("NOT", onOperator)
        LogicButton("NEG", onOperator)
    }
}

@Composable
private fun HexLettersRow(onDigit: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonSpacing),
    ) {
        HexButton("A", onDigit)
        HexButton("B", onDigit)
        HexButton("C", onDigit)
        HexButton("D", onDigit)
        HexButton("E", onDigit)
        HexButton("F", onDigit)
    }
}

@Composable
private fun ControlRow(
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onOpenParen: () -> Unit,
    onCloseParen: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonSpacing),
    ) {
        CalcButton(
            text = "AC",
            onClick = onClear,
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
        )
        CalcButton(
            text = "(",
            onClick = onOpenParen,
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        CalcButton(
            text = ")",
            onClick = onCloseParen,
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        CalcButton(
            text = "\u232B",
            onClick = onDelete,
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun NumberRows(
    currentBase: NumberBase,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
) {
    // Row: 7, 8, 9, ÷
    KeypadRow {
        NumButton("7", enabled = currentBase.ordinal >= NumberBase.OCT.ordinal, onDigit = onDigit)
        NumButton("8", enabled = currentBase.ordinal >= NumberBase.DEC.ordinal, onDigit = onDigit)
        NumButton("9", enabled = currentBase.ordinal >= NumberBase.DEC.ordinal, onDigit = onDigit)
        OpButton("\u00F7", onOperator)
    }
    // Row: 4, 5, 6, ×
    KeypadRow {
        NumButton("4", enabled = currentBase.ordinal >= NumberBase.OCT.ordinal, onDigit = onDigit)
        NumButton("5", enabled = currentBase.ordinal >= NumberBase.OCT.ordinal, onDigit = onDigit)
        NumButton("6", enabled = currentBase.ordinal >= NumberBase.OCT.ordinal, onDigit = onDigit)
        OpButton("\u00D7", onOperator)
    }
    // Row: 1, 2, 3, -
    KeypadRow {
        NumButton("1", enabled = true, onDigit = onDigit)
        NumButton("2", enabled = currentBase.ordinal >= NumberBase.OCT.ordinal, onDigit = onDigit)
        NumButton("3", enabled = currentBase.ordinal >= NumberBase.OCT.ordinal, onDigit = onDigit)
        OpButton("\u2212", onOperator)
    }
}

@Composable
private fun BottomRow(
    @Suppress("UNUSED_PARAMETER") currentBase: NumberBase,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onEquals: () -> Unit,
) {
    KeypadRow {
        NumButton("0", enabled = true, onDigit = onDigit)
        OpButton("+", onOperator)
        CalcButton(
            text = "=",
            onClick = onEquals,
            modifier = Modifier.weight(2f),
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            textStyle = MaterialTheme.typography.headlineSmall,
        )
    }
}

// ---------------------------------------------------------------
// Reusable helpers
// ---------------------------------------------------------------

@Composable
private fun KeypadRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonSpacing),
        content = content,
    )
}

@Composable
private fun RowScope.NumButton(
    digit: String,
    enabled: Boolean,
    onDigit: (String) -> Unit,
) {
    CalcButton(
        text = digit,
        onClick = { if (enabled) onDigit(digit) },
        modifier = Modifier.weight(1f),
        backgroundColor = if (enabled) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        },
        contentColor = if (enabled) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        },
    )
}

@Composable
private fun RowScope.OpButton(symbol: String, onOperator: (String) -> Unit) {
    CalcButton(
        text = symbol,
        onClick = { onOperator(symbol) },
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun RowScope.LogicButton(label: String, onOperator: (String) -> Unit) {
    CalcButton(
        text = label,
        onClick = { onOperator(label) },
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        textStyle = MaterialTheme.typography.bodySmall,
    )
}

@Composable
private fun RowScope.HexButton(letter: String, onDigit: (String) -> Unit) {
    CalcButton(
        text = letter,
        onClick = { onDigit(letter) },
        modifier = Modifier.weight(1f),
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
}
