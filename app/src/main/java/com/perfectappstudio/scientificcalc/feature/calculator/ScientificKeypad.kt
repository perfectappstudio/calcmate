package com.perfectappstudio.scientificcalc.feature.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.core.model.AngleUnit
import com.perfectappstudio.scientificcalc.core.model.CalculatorAction
import com.perfectappstudio.scientificcalc.core.model.CalculatorState
import com.perfectappstudio.scientificcalc.ui.components.CalcButton
import com.perfectappstudio.scientificcalc.ui.components.CalcButtonVariant
import com.perfectappstudio.scientificcalc.ui.theme.PurpleAccent

@Composable
fun ScientificKeypad(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            ToggleKey(when (state.angleUnit) {
                AngleUnit.DEGREE -> "DEG"
                AngleUnit.RADIAN -> "RAD"
                AngleUnit.GRADIAN -> "GRAD"
            }, state.angleUnit != AngleUnit.DEGREE) {
                onAction(CalculatorAction.ToggleAngleUnit)
            }
            ToggleKey("INV", state.isInverse) { onAction(CalculatorAction.ToggleInverse) }
            ToggleKey("HYP", state.isHyperbolic) { onAction(CalculatorAction.ToggleHyperbolic) }
            Spacer(Modifier.weight(1f))
            Text(if (state.mIndicator) "M" else "", style = MaterialTheme.typography.labelMedium)
        }
        AnimatedVisibility(visible = state.isScientificExpanded) {
          if (compact) {
            val trig = buildTrigLabels(state.isInverse, state.isHyperbolic)
            val keys = listOf(
                trig[0] to CalculatorAction.Function("sin"), trig[1] to CalculatorAction.Function("cos"),
                trig[2] to CalculatorAction.Function("tan"), "ln" to CalculatorAction.Function("ln"),
                "log" to CalculatorAction.Function("log"), "√" to CalculatorAction.Function("sqrt"),
                "π" to CalculatorAction.Constant("π"), "e" to CalculatorAction.Constant("e"),
                "xʸ" to CalculatorAction.Operator("^"), "x!" to CalculatorAction.Operator("!"),
                "(" to CalculatorAction.OpenParen, ")" to CalculatorAction.CloseParen,
                "M+" to CalculatorAction.AddToM, "M−" to CalculatorAction.SubtractFromM,
                "MR" to CalculatorAction.RecallM, "±" to CalculatorAction.ToggleSign,
                "%" to CalculatorAction.Operator("%"), "FMT" to CalculatorAction.ToggleDisplayFormat,
            )
            Row(modifier = Modifier.fillMaxWidth().height(48.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                keys.forEach { (label, action) ->
                    CalcButton(label, { onAction(action) }, Modifier.width(56.dp).height(48.dp), variant = CalcButtonVariant.Scientific)
                }
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val trig = buildTrigLabels(state.isInverse, state.isHyperbolic)
                KeyRow {
                    FunctionKey(trig[0], "sin", onAction)
                    FunctionKey(trig[1], "cos", onAction)
                    FunctionKey(trig[2], "tan", onAction)
                    FunctionKey("ln", "ln", onAction)
                    FunctionKey("log", "log", onAction)
                    FunctionKey("√", "sqrt", onAction)
                }
                KeyRow {
                    ScienceKey("π", CalculatorAction.Constant("π"), onAction)
                    ScienceKey("e", CalculatorAction.Constant("e"), onAction)
                    ScienceKey("xʸ", CalculatorAction.Operator("^"), onAction)
                    ScienceKey("x!", CalculatorAction.Operator("!"), onAction)
                    ScienceKey("(", CalculatorAction.OpenParen, onAction)
                    ScienceKey(")", CalculatorAction.CloseParen, onAction)
                }
                KeyRow {
                    ScienceKey("M+", CalculatorAction.AddToM, onAction)
                    ScienceKey("M−", CalculatorAction.SubtractFromM, onAction)
                    ScienceKey("MR", CalculatorAction.RecallM, onAction)
                    ScienceKey("±", CalculatorAction.ToggleSign, onAction)
                    ScienceKey("%", CalculatorAction.Operator("%"), onAction)
                    ScienceKey("FMT", CalculatorAction.ToggleDisplayFormat, onAction)
                }
            }
          }
        }
        KeyRow(compact) {
            NumberKey("7", onAction); NumberKey("8", onAction); NumberKey("9", onAction)
            CalcButton("DEL", { onAction(CalculatorAction.Backspace) }, Modifier.weight(1f),
                accessibilityLabel = "⌫", backgroundColor = Color(0xFFE5BA7B), contentColor = Color(0xFF342516))
            CalcButton("AC", { onAction(CalculatorAction.Clear) }, Modifier.weight(1f),
                accessibilityLabel = "C", backgroundColor = Color(0xFFE5BA7B), contentColor = Color(0xFF342516))
        }
        KeyRow(compact) {
            NumberKey("4", onAction); NumberKey("5", onAction); NumberKey("6", onAction)
            OperatorKey("×", onAction); OperatorKey("÷", onAction)
        }
        KeyRow(compact) {
            NumberKey("1", onAction); NumberKey("2", onAction); NumberKey("3", onAction)
            OperatorKey("+", onAction); OperatorKey("−", onAction)
        }
        KeyRow(compact) {
            NumberKey("0", onAction)
            CalcButton(".", { onAction(CalculatorAction.Decimal) }, Modifier.weight(1f),
                backgroundColor = Color(0xFFE3EBED), contentColor = Color(0xFF14232D))
            ScienceKey("Ans", CalculatorAction.Constant("Ans"), onAction)
            CalcButton("±", { onAction(CalculatorAction.ToggleSign) }, Modifier.weight(1f), variant = CalcButtonVariant.Operator)
            CalcButton("=", { onAction(CalculatorAction.Equals) }, Modifier.weight(1f),
                variant = CalcButtonVariant.Equals, backgroundColor = PurpleAccent, contentColor = Color(0xFF101B22))
        }
        if (!state.isScientificExpanded) {
            KeyRow(compact) {
                ScienceKey("(", CalculatorAction.OpenParen, onAction)
                ScienceKey(")", CalculatorAction.CloseParen, onAction)
                ScienceKey("%", CalculatorAction.Operator("%"), onAction)
            }
        }
    }
}

@Composable
private fun KeyRow(compact: Boolean = false, content: @Composable RowScope.() -> Unit) {
    Row(Modifier.fillMaxWidth().then(if (compact) Modifier.height(48.dp) else Modifier),
        horizontalArrangement = Arrangement.spacedBy(6.dp), content = content)
}

@Composable
private fun RowScope.NumberKey(label: String, onAction: (CalculatorAction) -> Unit) {
    CalcButton(label, { onAction(CalculatorAction.Digit(label)) }, Modifier.weight(1f),
        backgroundColor = Color(0xFFE3EBED), contentColor = Color(0xFF14232D),
        textStyle = MaterialTheme.typography.headlineSmall)
}

@Composable
private fun RowScope.OperatorKey(label: String, onAction: (CalculatorAction) -> Unit) {
    CalcButton(label, { onAction(CalculatorAction.Operator(label)) }, Modifier.weight(1f),
        variant = CalcButtonVariant.Operator, textStyle = MaterialTheme.typography.headlineSmall)
}

@Composable
private fun RowScope.FunctionKey(label: String, function: String, onAction: (CalculatorAction) -> Unit) {
    ScienceKey(label, CalculatorAction.Function(function), onAction)
}

@Composable
private fun RowScope.ScienceKey(label: String, action: CalculatorAction, onAction: (CalculatorAction) -> Unit) {
    CalcButton(label, { onAction(action) }, Modifier.weight(1f), variant = CalcButtonVariant.Scientific)
}

@Composable
private fun ToggleKey(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(selectedLabelColor = Color(0xFF101B22)))
}

private fun buildTrigLabels(isInverse: Boolean, isHyperbolic: Boolean): List<String> =
    listOf("sin", "cos", "tan").map { name ->
        when {
            isInverse && isHyperbolic -> "a${name}h"
            isInverse -> "a$name"
            isHyperbolic -> "${name}h"
            else -> name
        }
    }
