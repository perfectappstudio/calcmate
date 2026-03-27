package com.perfectappstudio.scientificcalc.feature.solver

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.perfectappstudio.scientificcalc.core.math.SystemResult
import com.perfectappstudio.scientificcalc.core.math.SystemResult3x3
import com.perfectappstudio.scientificcalc.core.model.SolverResult
import com.perfectappstudio.scientificcalc.core.model.SolverState
import com.perfectappstudio.scientificcalc.core.model.SolverType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemSolverPanel(
    state: SolverState,
    onTypeChange: (SolverType) -> Unit,
    on2x2CoefficientChange: (Int, String) -> Unit,
    on3x3CoefficientChange: (Int, String) -> Unit,
    onSolve: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val is3x3 = state.solverType == SolverType.SYSTEM_3X3

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "System of Linear Equations",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        // --- 2x2 / 3x3 toggle chips ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            FilterChip(
                selected = !is3x3,
                onClick = { onTypeChange(SolverType.SYSTEM_2X2) },
                label = { Text("2 \u00D7 2") },
            )
            FilterChip(
                selected = is3x3,
                onClick = { onTypeChange(SolverType.SYSTEM_3X3) },
                label = { Text("3 \u00D7 3") },
            )
        }

        if (!is3x3) {
            System2x2Inputs(state, on2x2CoefficientChange)
        } else {
            System3x3Inputs(state, on3x3CoefficientChange)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSolve,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Solve")
        }

        Spacer(Modifier.height(16.dp))

        state.result?.let { result ->
            SystemResultCard(result)
        }
    }
}

// --- 2x2 input grid ---

@Composable
private fun System2x2Inputs(
    state: SolverState,
    onChange: (Int, String) -> Unit,
) {
    // Equation 1: a1*x + b1*y = c1
    EquationRow(
        labels = listOf("a\u2081", "b\u2081", "c\u2081"),
        values = listOf(state.system2x2[0], state.system2x2[1], state.system2x2[2]),
        onChange = { idx, value -> onChange(idx, value) },
        indexOffset = 0,
        variables = "x + ",
        variable2 = "y = ",
    )
    Spacer(Modifier.height(8.dp))
    // Equation 2: a2*x + b2*y = c2
    EquationRow(
        labels = listOf("a\u2082", "b\u2082", "c\u2082"),
        values = listOf(state.system2x2[3], state.system2x2[4], state.system2x2[5]),
        onChange = { idx, value -> onChange(idx + 3, value) },
        indexOffset = 0,
        variables = "x + ",
        variable2 = "y = ",
    )
}

// --- 3x3 input grid ---

@Composable
private fun System3x3Inputs(
    state: SolverState,
    onChange: (Int, String) -> Unit,
) {
    for (row in 0 until 3) {
        val offset = row * 4
        val subscript = "\u2080${row + 1}" // subscript approximation
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            for (col in 0 until 4) {
                val idx = offset + col
                val label = when (col) {
                    0 -> "x"
                    1 -> "y"
                    2 -> "z"
                    else -> "="
                }
                if (col == 3) {
                    Text(
                        text = "=",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                OutlinedTextField(
                    value = state.system3x3[idx],
                    onValueChange = { onChange(idx, it) },
                    label = { Text(if (col < 3) label else "b") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                if (col < 2) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        if (row < 2) Spacer(Modifier.height(8.dp))
    }
}

// --- Shared equation row composable ---

@Composable
private fun EquationRow(
    labels: List<String>,
    values: List<String>,
    onChange: (Int, String) -> Unit,
    indexOffset: Int,
    variables: String,
    variable2: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = values[0],
            onValueChange = { onChange(0, it) },
            label = { Text(labels[0]) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = variables,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = values[1],
            onValueChange = { onChange(1, it) },
            label = { Text(labels[1]) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = variable2,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = values[2],
            onValueChange = { onChange(2, it) },
            label = { Text(labels[2]) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
    }
}

// --- Result card ---

@Composable
private fun SystemResultCard(result: SolverResult) {
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
                is SolverResult.System2x2 -> System2x2ResultBody(result.result)
                is SolverResult.System3x3 -> System3x3ResultBody(result.result)
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
private fun System2x2ResultBody(result: SystemResult) {
    when (result) {
        is SystemResult.Solution -> {
            Text(
                text = "x = ${formatNumber(result.x)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "y = ${formatNumber(result.y)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        is SystemResult.NoSolution -> {
            Text(
                text = "No solution",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "The system is inconsistent (parallel lines)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        is SystemResult.InfiniteSolutions -> {
            Text(
                text = "Infinite solutions",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "The equations represent the same line",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun System3x3ResultBody(result: SystemResult3x3) {
    when (result) {
        is SystemResult3x3.Solution -> {
            Text(
                text = "x = ${formatNumber(result.x)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "y = ${formatNumber(result.y)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "z = ${formatNumber(result.z)}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        is SystemResult3x3.NoSolution -> {
            Text(
                text = "No solution",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "The system is inconsistent",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        is SystemResult3x3.InfiniteSolutions -> {
            Text(
                text = "Infinite solutions",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "The system has a dependent equation",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
