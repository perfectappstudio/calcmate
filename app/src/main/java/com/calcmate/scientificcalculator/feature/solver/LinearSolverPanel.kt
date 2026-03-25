package com.calcmate.scientificcalculator.feature.solver

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.calcmate.scientificcalculator.core.math.LinearResult
import com.calcmate.scientificcalculator.core.model.SolverResult
import com.calcmate.scientificcalculator.core.model.SolverState

@Composable
fun LinearSolverPanel(
    state: SolverState,
    onCoefficientChange: (Int, String) -> Unit,
    onSolve: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Linear Equation: ax + b = 0",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = state.linearA,
                onValueChange = { onCoefficientChange(0, it) },
                label = { Text("a") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "x +",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                value = state.linearB,
                onValueChange = { onCoefficientChange(1, it) },
                label = { Text("b") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "= 0",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
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

        // --- Result card ---
        state.result?.let { result ->
            LinearResultCard(result)
        }
    }
}

@Composable
private fun LinearResultCard(result: SolverResult) {
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
                is SolverResult.Linear -> {
                    when (val lr = result.result) {
                        is LinearResult.Solution -> {
                            Text(
                                text = "x = ${formatNumber(lr.x)}",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        is LinearResult.NoSolution -> {
                            Text(
                                text = "No solution",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                text = "The equation is inconsistent (0 = non-zero)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        is LinearResult.InfiniteSolutions -> {
                            Text(
                                text = "Infinite solutions",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                text = "The equation 0 = 0 is always true",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                is SolverResult.Error -> {
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                else -> { /* other result types not shown here */ }
            }
        }
    }
}

/** Format a Double to 4 decimal places, stripping trailing zeros. */
internal fun formatNumber(value: Double): String {
    if (value == -0.0) return "0"
    val s = "%.4f".format(value)
    return s.trimEnd('0').trimEnd('.')
}
