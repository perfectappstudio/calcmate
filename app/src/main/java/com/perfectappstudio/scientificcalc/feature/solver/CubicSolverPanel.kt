package com.perfectappstudio.scientificcalc.feature.solver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.core.math.CubicResult
import com.perfectappstudio.scientificcalc.core.model.SolverResult
import com.perfectappstudio.scientificcalc.core.model.SolverState

private val GreenRoots = Color(0xFF4CAF50)
private val AmberRepeated = Color(0xFFFFC107)
private val RedComplex = Color(0xFFEF5350)

@Composable
fun CubicSolverPanel(
    state: SolverState,
    onCoefficientChange: (Int, String) -> Unit,
    onSolve: () -> Unit,
    onToggleSteps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Cubic Equation: ax\u00B3 + bx\u00B2 + cx + d = 0",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // Four coefficient inputs in a row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = state.cubA,
                onValueChange = { onCoefficientChange(0, it) },
                label = { Text("a") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = state.cubB,
                onValueChange = { onCoefficientChange(1, it) },
                label = { Text("b") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = state.cubC,
                onValueChange = { onCoefficientChange(2, it) },
                label = { Text("c") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = state.cubD,
                onValueChange = { onCoefficientChange(3, it) },
                label = { Text("d") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSolve,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Solve")
        }

        Spacer(Modifier.height(16.dp))

        // Result card
        state.result?.let { result ->
            CubicResultCard(
                result = result,
                showSteps = state.showSteps,
                onToggleSteps = onToggleSteps,
            )
        }
    }
}

@Composable
private fun CubicResultCard(
    result: SolverResult,
    showSteps: Boolean,
    onToggleSteps: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Result",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            when (result) {
                is SolverResult.Cubic -> CubicBody(result.result, showSteps, onToggleSteps)
                is SolverResult.Error -> {
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                else -> { /* not applicable */ }
            }
        }
    }
}

@Composable
private fun CubicBody(
    cr: CubicResult,
    showSteps: Boolean,
    onToggleSteps: () -> Unit,
) {
    when (cr) {
        is CubicResult.ThreeRealRoots -> {
            // Check if roots are repeated
            val allEqual = formatNumber(cr.x1) == formatNumber(cr.x2) &&
                formatNumber(cr.x2) == formatNumber(cr.x3)
            val twoEqual = !allEqual && (
                formatNumber(cr.x1) == formatNumber(cr.x2) ||
                    formatNumber(cr.x2) == formatNumber(cr.x3) ||
                    formatNumber(cr.x1) == formatNumber(cr.x3)
                )

            val color = when {
                allEqual || twoEqual -> AmberRepeated
                else -> GreenRoots
            }
            val description = when {
                allEqual -> "Triple repeated root"
                twoEqual -> "One repeated root"
                else -> "Three distinct real roots"
            }

            RootsBadge(description = description, color = color)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "x\u2081 = ${formatNumber(cr.x1)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "x\u2082 = ${formatNumber(cr.x2)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "x\u2083 = ${formatNumber(cr.x3)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            CubicStepsSection(cr.steps, showSteps, onToggleSteps)
        }

        is CubicResult.OneRealTwoComplex -> {
            RootsBadge(description = "One real root, two complex conjugate roots", color = RedComplex)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "x\u2081 = ${formatNumber(cr.x1)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "x\u2082,\u2083 = ${formatNumber(cr.realPart)} \u00B1 ${formatNumber(cr.imagPart)}i",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            CubicStepsSection(cr.steps, showSteps, onToggleSteps)
        }

        is CubicResult.DegenerateQuadratic -> {
            Text(
                text = "Degenerates to quadratic (a = 0)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = "Use the Quadratic solver for this equation.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun RootsBadge(description: String, color: Color) {
    Text(
        text = description,
        style = MaterialTheme.typography.labelLarge,
        color = color,
    )
}

@Composable
private fun CubicStepsSection(
    steps: List<String>,
    showSteps: Boolean,
    onToggleSteps: () -> Unit,
) {
    Spacer(Modifier.height(8.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
    TextButton(
        onClick = onToggleSteps,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(if (showSteps) "Hide steps" else "Show steps")
    }
    AnimatedVisibility(
        visible = showSteps,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp),
        ) {
            steps.forEach { step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
